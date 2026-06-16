package su.nightexpress.excellentcrates.war;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.api.crate.Reward;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.ArrayList;
import java.util.List;

/**
 * CS:GO-style battle view for a resolved crate war. The top row is the opponent's spinner
 * ("maszyna CS:GO"), the bottom row is the viewer's own spinner. Each staked key is animated
 * as its own spin: both strips scroll and decelerate, land on that round's reward, the running
 * score ticks up, and after the last round the winner is revealed.
 *
 * <p>This menu is purely visual: the winner is already decided and the actual rewards are
 * distributed by {@link WarManager} after {@link #durationTicks(int)}.</p>
 */
public class WarBattleMenu implements InventoryHolder {

    private static final int SIZE = 54;
    private static final int STEPS = 24;          // distinct "centers" each spin passes through
    private static final int PAD = 4;             // fillers on each side of the visible window
    private static final long END_PAUSE = 30L;    // ticks the result stays before payout

    private static final int[] TOP_SLOTS    = {9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int[] BOTTOM_SLOTS = {36, 37, 38, 39, 40, 41, 42, 43, 44};

    private final CratesPlugin plugin;
    private final Player viewer;
    private final Player opponent;
    private final Crate  crate;

    private final List<ItemStack> selfIcons;
    private final List<ItemStack> oppIcons;
    private final double[] selfPoints;
    private final double[] oppPoints;
    private final String winnerName; // null => draw

    private final Inventory inventory;
    private final int rounds;
    private final long roundTicks;

    private List<ItemStack> pool;
    private ItemStack[] topStrip;
    private ItemStack[] bottomStrip;

    private BukkitTask task;
    private int round;
    private long roundElapsed;
    private int lastIndex = -1;
    private double selfRunning;
    private double oppRunning;
    private boolean finished;
    private long endCounter;

    public WarBattleMenu(@NotNull CratesPlugin plugin,
                         @NotNull Player viewer,
                         @NotNull Player opponent,
                         @NotNull Crate crate,
                         @NotNull List<Reward> selfRolls,
                         @NotNull double[] selfPoints,
                         @NotNull List<Reward> oppRolls,
                         @NotNull double[] oppPoints,
                         @Nullable String winnerName) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.opponent = opponent;
        this.crate = crate;
        this.selfPoints = selfPoints;
        this.oppPoints = oppPoints;
        this.winnerName = winnerName;

        this.selfIcons = toIcons(selfRolls);
        this.oppIcons = toIcons(oppRolls);
        this.rounds = Math.max(1, Math.min(this.selfIcons.size(), this.oppIcons.size()));
        this.roundTicks = roundTicks(this.rounds);

        String title = "§0§lCRATE WAR §8» §c" + viewer.getName() + " §7vs §c" + opponent.getName();
        this.inventory = Bukkit.createInventory(this, SIZE, title.length() > 32 ? "§0§lCRATE WAR" : title);
    }

    /** Per-round spin length, in ticks; shrinks as the stake grows so big wars stay watchable. */
    private static long roundTicks(int rounds) {
        return Math.max(6L, Math.min(45L, 180L / Math.max(1, rounds)));
    }

    /** Total animation length for a war of the given key amount. */
    public static long durationTicks(int amount) {
        int rounds = Math.max(1, amount);
        return rounds * roundTicks(rounds) + END_PAUSE;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void start() {
        this.buildFrame();
        this.pool = this.buildPool();
        this.beginRound(0);

        this.viewer.openInventory(this.inventory);
        this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, this::tick, 1L, 1L);
    }

    @NotNull
    private static List<ItemStack> toIcons(@NotNull List<Reward> rolls) {
        List<ItemStack> icons = new ArrayList<>();
        for (Reward reward : rolls) {
            ItemStack item = reward.getPreviewItem();
            icons.add(item != null && item.getType() != Material.AIR ? item : new ItemStack(Material.PAPER));
        }
        return icons;
    }

    @NotNull
    private List<ItemStack> buildPool() {
        List<ItemStack> pool = new ArrayList<>();
        for (Reward reward : this.crate.getRewards(this.viewer)) {
            ItemStack item = reward.getPreviewItem();
            if (item != null && item.getType() != Material.AIR) pool.add(item);
        }
        pool.addAll(this.selfIcons);
        pool.addAll(this.oppIcons);
        if (pool.isEmpty()) pool.add(new ItemStack(Material.PAPER));
        return pool;
    }

    private void beginRound(int round) {
        this.round = round;
        this.roundElapsed = 0;
        this.lastIndex = -1;
        this.topStrip = this.buildStrip(this.oppIcons.get(round));
        this.bottomStrip = this.buildStrip(this.selfIcons.get(round));
        this.updateHeads();
    }

    /** A strip of random fillers with the target placed where it lands under the pointer. */
    @NotNull
    private ItemStack[] buildStrip(@NotNull ItemStack target) {
        int length = STEPS + PAD * 2;
        ItemStack[] strip = new ItemStack[length];
        for (int i = 0; i < length; i++) {
            strip[i] = this.pool.get(Rnd.get(this.pool.size())).clone();
        }
        strip[(STEPS - 1) + PAD] = target.clone();
        return strip;
    }

    private void tick() {
        // Stop if the viewer closed the battle view.
        if (!this.viewer.isOnline() || this.viewer.getOpenInventory().getTopInventory() != this.inventory) {
            this.cancel();
            return;
        }

        if (this.finished) {
            if (++this.endCounter >= END_PAUSE) this.cancel();
            return;
        }

        double progress = Math.min(1.0D, (double) this.roundElapsed / (double) this.roundTicks);
        double eased = 1.0D - Math.pow(1.0D - progress, 3); // easeOutCubic deceleration
        int index = (int) Math.round(eased * (STEPS - 1));

        if (index != this.lastIndex) {
            this.renderStrip(TOP_SLOTS, this.topStrip, index);
            this.renderStrip(BOTTOM_SLOTS, this.bottomStrip, index);
            this.viewer.playSound(this.viewer.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.6f);
            this.lastIndex = index;
        }

        if (this.roundElapsed >= this.roundTicks) {
            this.endRound();
            return;
        }

        this.roundElapsed++;
    }

    private void endRound() {
        // Bank this round's points and ding.
        this.selfRunning += this.selfPoints[this.round];
        this.oppRunning += this.oppPoints[this.round];
        this.updateHeads();
        this.viewer.playSound(this.viewer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.4f);

        if (this.round + 1 < this.rounds) {
            this.beginRound(this.round + 1);
        }
        else {
            this.finish();
        }
    }

    private void renderStrip(int[] slots, @NotNull ItemStack[] strip, int index) {
        for (int i = 0; i < slots.length; i++) {
            int stripIndex = index + i;
            this.inventory.setItem(slots[i], stripIndex < strip.length ? strip[stripIndex] : null);
        }
    }

    private void finish() {
        this.finished = true;

        boolean draw = this.winnerName == null;
        boolean won = !draw && this.winnerName.equals(this.viewer.getName());

        ItemStack status;
        if (draw) {
            status = named(Material.PAPER, "§e§lREMIS");
        }
        else if (won) {
            status = named(Material.NETHER_STAR, "§a§lWYGRANA!");
            this.viewer.playSound(this.viewer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }
        else {
            status = named(Material.BARRIER, "§c§lPRZEGRANA");
            this.viewer.playSound(this.viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
        this.inventory.setItem(8, status);
        this.inventory.setItem(53, status.clone());
    }

    private void buildFrame() {
        ItemStack border = named(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int slot = 0; slot < SIZE; slot++) {
            this.inventory.setItem(slot, border.clone());
        }

        ItemStack pointer = named(Material.HOPPER, "§e▼");
        this.inventory.setItem(4, pointer.clone());   // above top strip
        this.inventory.setItem(22, pointer.clone());  // below top strip
        this.inventory.setItem(31, pointer.clone());  // above bottom strip
        this.inventory.setItem(49, pointer.clone());  // below bottom strip

        // Clear strip rows so the border doesn't flash before the first frame.
        for (int slot : TOP_SLOTS) this.inventory.setItem(slot, null);
        for (int slot : BOTTOM_SLOTS) this.inventory.setItem(slot, null);
    }

    private void updateHeads() {
        int shown = this.finished ? this.rounds : Math.min(this.round + 1, this.rounds);
        this.inventory.setItem(0, this.head(this.opponent, "§c" + this.opponent.getName(), this.oppRunning, shown));
        this.inventory.setItem(45, this.head(this.viewer, "§a" + this.viewer.getName() + " §7(Ty)", this.selfRunning, shown));
    }

    @NotNull
    private ItemStack head(@NotNull Player player, @NotNull String name, double score, int shownRound) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.editMeta(item, meta -> {
            if (meta instanceof SkullMeta skull) skull.setOwningPlayer(player);
            ItemUtil.setCustomName(meta, name);
            ItemUtil.setLore(meta, List.of(
                "§7Runda: §e" + shownRound + "§7/§e" + this.rounds,
                "§7Wynik: §e" + (long) score + " pkt"
            ));
        });
        return item;
    }

    @NotNull
    private static ItemStack named(@NotNull Material material, @NotNull String name) {
        ItemStack item = new ItemStack(material);
        ItemUtil.editMeta(item, meta -> ItemUtil.setCustomName(meta, name));
        return item;
    }

    private void cancel() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }
}
