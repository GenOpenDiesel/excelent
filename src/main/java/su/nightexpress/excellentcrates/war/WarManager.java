package su.nightexpress.excellentcrates.war;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.api.crate.Reward;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.cost.Cost;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.util.FoliaTasks;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WarManager extends AbstractManager<CratesPlugin> {

    // Pending invitations keyed by the target (invited) player's id. A player can have only one pending invite at a time.
    private final Map<UUID, CrateWar> invitesByTarget;

    // Challengers who confirmed a key amount in the GUI and are now typing the opponent's name in chat.
    private final Map<UUID, PendingChallenge> pendingByChallenger;

    public WarManager(@NotNull CratesPlugin plugin) {
        super(plugin);
        this.invitesByTarget = new ConcurrentHashMap<>();
        this.pendingByChallenger = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        // Captures the opponent name typed in chat after clicking "graj o wojnę".
        this.addListener(new WarListener(this.plugin, this));

        // Periodically drop expired invitations / chat prompts and notify both sides.
        this.addAsyncTask(this::tickInvites, 20L);
    }

    @Override
    protected void onShutdown() {
        this.invitesByTarget.clear();
        this.pendingByChallenger.clear();
    }

    private void tickInvites() {
        this.invitesByTarget.values().removeIf(war -> {
            if (!war.isExpired()) return false;

            Player challenger = this.plugin.getServer().getPlayer(war.getChallengerId());
            Player target = this.plugin.getServer().getPlayer(war.getTargetId());

            if (challenger != null) Lang.WAR_EXPIRED.message().send(challenger, replacer -> this.applyWarPlaceholders(replacer, war));
            if (target != null) Lang.WAR_EXPIRED.message().send(target, replacer -> this.applyWarPlaceholders(replacer, war));
            return true;
        });

        // Quietly drop stale chat prompts (the challenger never typed a name in time).
        this.pendingByChallenger.values().removeIf(PendingChallenge::isExpired);
    }

    private void applyWarPlaceholders(@NotNull su.nightexpress.nightcore.util.placeholder.Replacer replacer, @NotNull CrateWar war) {
        replacer
            .replace(Placeholders.WAR_CHALLENGER, war.getChallengerName())
            .replace(Placeholders.WAR_TARGET, war.getTargetName())
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(war.getAmount()))
            .replace(Placeholders.CRATE_ID, war.getCrateId());
    }

    @Nullable
    public CrateWar getInvite(@NotNull Player target) {
        CrateWar war = this.invitesByTarget.get(target.getUniqueId());
        if (war != null && war.isExpired()) {
            this.invitesByTarget.remove(target.getUniqueId());
            return null;
        }
        return war;
    }

    /**
     * Called from the GUI after the challenger picks the key amount and clicks confirm.
     * Validates the stake and then asks the challenger to type the opponent's name in chat.
     */
    public void promptTarget(@NotNull Player challenger, @NotNull Crate crate, int amount) {
        if (!Config.WAR_ENABLED.get()) {
            Lang.WAR_DISABLED.message().send(challenger);
            return;
        }

        int min = Config.WAR_MIN_KEYS.get();
        int max = Config.WAR_MAX_KEYS.get();
        if (amount < min || amount > max) {
            Lang.WAR_ERROR_AMOUNT.message().send(challenger, replacer -> replacer
                .replace(Placeholders.GENERIC_MIN, NumberUtil.format(min))
                .replace(Placeholders.GENERIC_MAX, NumberUtil.format(max))
            );
            return;
        }

        // The key type is exactly the crate's own cost (the crate shown in the preview GUI).
        Cost cost = crate.getFirstCost().orElse(null);
        if (cost == null) {
            Lang.WAR_ERROR_NO_COST.message().send(challenger, replacer -> replacer.replace(crate.replacePlaceholders()));
            return;
        }

        if (!crate.hasRewards(challenger)) {
            Lang.WAR_ERROR_NO_REWARDS.message().send(challenger, replacer -> replacer.replace(crate.replacePlaceholders()));
            return;
        }

        if (cost.countMaxOpenings(challenger) < amount) {
            Lang.WAR_ERROR_NOT_ENOUGH_KEYS.message().send(challenger, replacer -> replacer
                .replace(crate.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            );
            return;
        }

        long expireAt = System.currentTimeMillis() + Config.WAR_INVITE_EXPIRE_SECONDS.get() * 1000L;
        this.pendingByChallenger.put(challenger.getUniqueId(), new PendingChallenge(crate.getId(), amount, expireAt));

        Lang.WAR_PROMPT_TARGET.message().send(challenger, replacer -> replacer
            .replace(crate.replacePlaceholders())
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
        );
    }

    public boolean isAwaitingTarget(@NotNull Player player) {
        PendingChallenge pending = this.pendingByChallenger.get(player.getUniqueId());
        if (pending != null && pending.isExpired()) {
            this.pendingByChallenger.remove(player.getUniqueId());
            return false;
        }
        return pending != null;
    }

    /**
     * Handles the opponent name typed in chat. Called from the (async) chat listener; returns
     * true if the message was consumed as a war-target input and the chat event should be cancelled.
     */
    public boolean handleTargetInput(@NotNull Player challenger, @NotNull String message) {
        PendingChallenge pending = this.pendingByChallenger.remove(challenger.getUniqueId());
        if (pending == null) return false;
        if (pending.isExpired()) return false;

        String input = message.trim();

        // Bukkit API isn't thread-safe; resolve and start the war on the main thread.
        this.plugin.runTask(task -> {
            if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("anuluj")) {
                Lang.WAR_PROMPT_CANCELLED.message().send(challenger);
                return;
            }

            Crate crate = this.plugin.getCrateManager().getCrateById(pending.crateId());
            if (crate == null) {
                Lang.WAR_PROMPT_CANCELLED.message().send(challenger);
                return;
            }

            Player target = this.plugin.getServer().getPlayerExact(input);
            if (target == null || !target.isOnline()) {
                Lang.WAR_ERROR_TARGET_NOT_FOUND.message().send(challenger, replacer -> replacer.replace(Placeholders.WAR_TARGET, input));
                return;
            }

            this.challenge(challenger, target, crate, pending.amount());
        });

        return true;
    }

    public void challenge(@NotNull Player challenger, @NotNull Player target, @NotNull Crate crate, int amount) {
        if (!Config.WAR_ENABLED.get()) {
            Lang.WAR_DISABLED.message().send(challenger);
            return;
        }

        if (challenger == target) {
            Lang.WAR_ERROR_SELF.message().send(challenger);
            return;
        }

        int min = Config.WAR_MIN_KEYS.get();
        int max = Config.WAR_MAX_KEYS.get();
        if (amount < min || amount > max) {
            Lang.WAR_ERROR_AMOUNT.message().send(challenger, replacer -> replacer
                .replace(Placeholders.GENERIC_MIN, NumberUtil.format(min))
                .replace(Placeholders.GENERIC_MAX, NumberUtil.format(max))
            );
            return;
        }

        Cost cost = crate.getFirstCost().orElse(null);
        if (cost == null) {
            Lang.WAR_ERROR_NO_COST.message().send(challenger, replacer -> replacer.replace(crate.replacePlaceholders()));
            return;
        }

        if (!crate.hasRewards(challenger)) {
            Lang.WAR_ERROR_NO_REWARDS.message().send(challenger, replacer -> replacer.replace(crate.replacePlaceholders()));
            return;
        }

        if (cost.countMaxOpenings(challenger) < amount) {
            Lang.WAR_ERROR_NOT_ENOUGH_KEYS.message().send(challenger, replacer -> replacer
                .replace(crate.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            );
            return;
        }

        // A player can only be a target of a single pending invitation.
        if (this.getInvite(target) != null) {
            Lang.WAR_ERROR_ALREADY_INVITED.message().send(challenger, replacer -> replacer.replace(Placeholders.WAR_TARGET, target.getName()));
            return;
        }

        long expireAt = System.currentTimeMillis() + Config.WAR_INVITE_EXPIRE_SECONDS.get() * 1000L;
        CrateWar war = new CrateWar(challenger, target, crate, amount, expireAt);
        this.invitesByTarget.put(target.getUniqueId(), war);

        Lang.WAR_INVITE_SENT.message().send(challenger, replacer -> replacer
            .replace(crate.replacePlaceholders())
            .replace(Placeholders.WAR_TARGET, target.getName())
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(expireAt, TimeFormatType.LITERAL))
        );

        Lang.WAR_INVITE_RECEIVED.message().send(target, replacer -> replacer
            .replace(crate.replacePlaceholders())
            .replace(Placeholders.WAR_CHALLENGER, challenger.getName())
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .replace(Placeholders.GENERIC_TIME, TimeFormats.formatDuration(expireAt, TimeFormatType.LITERAL))
        );
    }

    public void deny(@NotNull Player target) {
        CrateWar war = this.getInvite(target);
        if (war == null) {
            Lang.WAR_ERROR_NO_INVITE.message().send(target);
            return;
        }

        this.invitesByTarget.remove(target.getUniqueId());

        Lang.WAR_DENIED_TARGET.message().send(target, replacer -> replacer.replace(Placeholders.WAR_CHALLENGER, war.getChallengerName()));

        Player challenger = this.plugin.getServer().getPlayer(war.getChallengerId());
        if (challenger != null) {
            Lang.WAR_DENIED_CHALLENGER.message().send(challenger, replacer -> replacer.replace(Placeholders.WAR_TARGET, target.getName()));
        }
    }

    public void accept(@NotNull Player target) {
        CrateWar war = this.getInvite(target);
        if (war == null) {
            Lang.WAR_ERROR_NO_INVITE.message().send(target);
            return;
        }

        Player challenger = this.plugin.getServer().getPlayer(war.getChallengerId());
        if (challenger == null) {
            this.invitesByTarget.remove(target.getUniqueId());
            Lang.WAR_ERROR_CHALLENGER_OFFLINE.message().send(target);
            return;
        }

        Crate crate = this.plugin.getCrateManager().getCrateById(war.getCrateId());
        if (crate == null) {
            this.invitesByTarget.remove(target.getUniqueId());
            Lang.WAR_ERROR_NO_INVITE.message().send(target);
            return;
        }

        Cost cost = crate.getFirstCost().orElse(null);
        if (cost == null) {
            this.invitesByTarget.remove(target.getUniqueId());
            Lang.WAR_ERROR_NO_COST.message().send(target, replacer -> replacer.replace(crate.replacePlaceholders()));
            return;
        }

        int amount = war.getAmount();

        // Re-validate that both sides can still afford their stake and have winnable rewards.
        if (!crate.hasRewards(challenger) || !crate.hasRewards(target)) {
            this.invitesByTarget.remove(target.getUniqueId());
            Lang.WAR_ERROR_NO_REWARDS.message().send(target, replacer -> replacer.replace(crate.replacePlaceholders()));
            return;
        }

        if (cost.countMaxOpenings(challenger) < amount) {
            this.invitesByTarget.remove(target.getUniqueId());
            Lang.WAR_ERROR_OPPONENT_BROKE.message().send(target, replacer -> replacer.replace(Placeholders.WAR_TARGET, challenger.getName()));
            Lang.WAR_ERROR_NOT_ENOUGH_KEYS.message().send(challenger, replacer -> replacer
                .replace(crate.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            );
            return;
        }

        if (cost.countMaxOpenings(target) < amount) {
            this.invitesByTarget.remove(target.getUniqueId());
            Lang.WAR_ERROR_NOT_ENOUGH_KEYS.message().send(target, replacer -> replacer
                .replace(crate.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            );
            Lang.WAR_ERROR_OPPONENT_BROKE.message().send(challenger, replacer -> replacer.replace(Placeholders.WAR_TARGET, target.getName()));
            return;
        }

        this.invitesByTarget.remove(target.getUniqueId());
        this.resolve(challenger, target, crate, cost, amount);
    }

    private void resolve(@NotNull Player challenger, @NotNull Player target, @NotNull Crate crate, @NotNull Cost cost, int amount) {
        // Take the stake from both players (one opening worth of cost per round).
        for (int i = 0; i < amount; i++) {
            cost.takeAll(challenger);
            cost.takeAll(target);
        }

        WarScore challengerScore = this.rollAndScore(challenger, crate, amount);
        WarScore targetScore = this.rollAndScore(target, crate, amount);

        // Winner = higher total rarity score. Tie is broken by the single rarest reward.
        Player winner = null;
        if (challengerScore.total > targetScore.total) {
            winner = challenger;
        }
        else if (targetScore.total > challengerScore.total) {
            winner = target;
        }
        else if (challengerScore.best > targetScore.best) {
            winner = challenger;
        }
        else if (targetScore.best > challengerScore.best) {
            winner = target;
        }

        Player finalWinner = winner;

        // Show the CS:GO-style battle to both players, then pay out once the animation lands.
        if (Config.WAR_ANIMATION_ENABLED.get() && !challengerScore.rewards.isEmpty() && !targetScore.rewards.isEmpty()) {
            String winnerName = winner == null ? null : winner.getName();

            new WarBattleMenu(this.plugin, challenger, target, crate,
                challengerScore.rewards, challengerScore.pointsArray(),
                targetScore.rewards, targetScore.pointsArray(), winnerName).start();

            new WarBattleMenu(this.plugin, target, challenger, crate,
                targetScore.rewards, targetScore.pointsArray(),
                challengerScore.rewards, challengerScore.pointsArray(), winnerName).start();

            Player schedulerOwner = finalWinner == null ? challenger : finalWinner;
            FoliaTasks.runDelayed(this.plugin, schedulerOwner, () -> {
                this.payout(challenger, target, challengerScore, targetScore, finalWinner);
                this.sendResult(challenger, target, crate, challengerScore, targetScore, finalWinner);
            }, () -> this.plugin.getLogger().warning("Could not finish crate war payout because the scheduler owner left before payout."), WarBattleMenu.durationTicks(amount) + 20L);
            return;
        }

        this.payout(challenger, target, challengerScore, targetScore, winner);
        this.sendResult(challenger, target, crate, challengerScore, targetScore, winner);
    }

    private void payout(@NotNull Player challenger,
                        @NotNull Player target,
                        @NotNull WarScore challengerScore,
                        @NotNull WarScore targetScore,
                        @Nullable Player winner) {
        if (winner == null) {
            // Perfect draw: everyone keeps their own rolled rewards.
            challengerScore.rewards.forEach(reward -> reward.giveContent(challenger));
            targetScore.rewards.forEach(reward -> reward.giveContent(target));
            return;
        }

        // Winner takes everything: both their own and the opponent's rolled rewards.
        List<Reward> spoils = new ArrayList<>(challengerScore.rewards);
        spoils.addAll(targetScore.rewards);
        spoils.forEach(reward -> reward.giveContent(winner));
    }

    @NotNull
    private WarScore rollAndScore(@NotNull Player player, @NotNull Crate crate, int amount) {
        WarScore score = new WarScore();
        for (int i = 0; i < amount; i++) {
            Reward reward = crate.rollReward(player);
            score.rewards.add(reward);

            // Rarer reward (smaller win chance) is worth more points.
            // Use the player-specific chance so it matches the pool actually rolled from
            // (permission-locked rewards are excluded, affecting everyone else's real odds).
            double chance = Math.max(reward.getRollChance(player), 0.0001D);
            double points = 100D / chance;

            score.points.add(points);
            score.total += points;
            if (points > score.best || score.bestReward == null) {
                score.best = points;
                score.bestReward = reward;
            }
        }
        return score;
    }

    private void sendResult(@NotNull Player challenger,
                            @NotNull Player target,
                            @NotNull Crate crate,
                            @NotNull WarScore challengerScore,
                            @NotNull WarScore targetScore,
                            @Nullable Player winner) {

        String winnerName = winner == null ? Lang.WAR_DRAW_NAME.text() : winner.getName();
        String loserName = winner == null ? Lang.WAR_DRAW_NAME.text() : (winner == challenger ? target.getName() : challenger.getName());

        java.util.function.Consumer<su.nightexpress.nightcore.util.placeholder.Replacer> apply = replacer -> replacer
            .replace(crate.replacePlaceholders())
            .replace(Placeholders.WAR_CHALLENGER, challenger.getName())
            .replace(Placeholders.WAR_TARGET, target.getName())
            .replace(Placeholders.WAR_WINNER, winnerName)
            .replace(Placeholders.WAR_LOSER, loserName)
            .replace(Placeholders.WAR_CHALLENGER_SCORE, NumberUtil.format(challengerScore.total))
            .replace(Placeholders.WAR_TARGET_SCORE, NumberUtil.format(targetScore.total));

        Lang.WAR_RESULT.message().send(challenger, apply);
        Lang.WAR_RESULT.message().send(target, apply);

        if (winner != null && Config.WAR_BROADCAST_RESULT.get()) {
            Lang.WAR_RESULT_BROADCAST.message().broadcast(apply);
        }
    }

    private static class WarScore {
        private final List<Reward> rewards = new ArrayList<>();
        private final List<Double> points = new ArrayList<>();
        private double total;
        private double best;
        private Reward bestReward;

        private double[] pointsArray() {
            double[] array = new double[this.points.size()];
            for (int i = 0; i < array.length; i++) array[i] = this.points.get(i);
            return array;
        }
    }

    private record PendingChallenge(@NotNull String crateId, int amount, long expireAt) {
        private boolean isExpired() {
            return System.currentTimeMillis() > this.expireAt;
        }
    }
}
