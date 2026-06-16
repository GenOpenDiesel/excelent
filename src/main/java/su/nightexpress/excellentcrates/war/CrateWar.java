package su.nightexpress.excellentcrates.war;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.crate.impl.Crate;

import java.util.UUID;

/**
 * Represents a pending crate war invitation between two players.
 * The challenger stakes a number of crate openings against the target;
 * once accepted both players open the same crate {@link #amount} times and
 * the player who rolls the rarer rewards takes everything.
 */
public class CrateWar {

    private final UUID   challengerId;
    private final String challengerName;
    private final UUID   targetId;
    private final String targetName;
    private final String crateId;
    private final int    amount;
    private final long   expireAt;

    public CrateWar(@NotNull Player challenger, @NotNull Player target, @NotNull Crate crate, int amount, long expireAt) {
        this.challengerId = challenger.getUniqueId();
        this.challengerName = challenger.getName();
        this.targetId = target.getUniqueId();
        this.targetName = target.getName();
        this.crateId = crate.getId();
        this.amount = amount;
        this.expireAt = expireAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.expireAt;
    }

    public boolean isChallenger(@NotNull Player player) {
        return player.getUniqueId().equals(this.challengerId);
    }

    public boolean isTarget(@NotNull Player player) {
        return player.getUniqueId().equals(this.targetId);
    }

    @NotNull
    public UUID getChallengerId() {
        return this.challengerId;
    }

    @NotNull
    public String getChallengerName() {
        return this.challengerName;
    }

    @NotNull
    public UUID getTargetId() {
        return this.targetId;
    }

    @NotNull
    public String getTargetName() {
        return this.targetName;
    }

    @NotNull
    public String getCrateId() {
        return this.crateId;
    }

    public int getAmount() {
        return this.amount;
    }

    public long getExpireAt() {
        return this.expireAt;
    }
}
