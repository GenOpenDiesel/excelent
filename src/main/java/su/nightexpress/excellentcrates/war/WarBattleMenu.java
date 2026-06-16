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
 * ("maszyna CS:GO"), the bottom row is the viewer's own spinner. Both strips scroll and
 * decelerate, landing on each side's rarest rolled reward, then the winner is revealed.
 *
 * <p>This menu is purely visual: the winner is already decided and the actual rewards are
 * distributed by {@link WarManager} after {@link #DURATION_TICKS}.</p>
 */
public class WarBattleMenu implements InventoryHolder {

    /** Total length of the spin animation, in ticks. {@link WarManager} pays out shortly after. */
    public static final long DURATION_TICKS = 100L;

    private static final int SIZE = 54;
    private static final int STEPS = 30;          // distinct "centers" the strip passes through
    private static final int PAD = 4;             // fillers on each side of the visible window

    // Strips occupy a full row; the pointer marks the center column (column index 4).
    private static final int[] TOP_SLOTS    = {9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int[] BOTTOM_SLOTS = {36, 37, 38, 39, 40, 41, 42, 43, 44};

    private final CratesPlugin plugin;
    private final Player viewer;
    private final Player opponent;
    private final Crate  crate;
    private final Reward selfTarget;
    private final Reward oppTarget;
    private final double selfScore;
    private final double oppScore;
    private final String winnerName; // null => draw

    private final Inventory inventory;

    private ItemStack[] topStrip;
    private ItemStack[] bottomStrip;
    private BukkitTask task;
    private long elapsed;
    private int lastIndex = -1;

    public WarBattleMenu(@NotNull CratesPlugin plugin,
                         @NotNull Player viewer,
                         @NotNull Player opponent,
                         @NotNull Crate crate,
                         @NotNull Reward selfTarget,
                         @NotNull Reward oppTarget,
                         double selfScore,
                         double oppScore,
                         @Nullable String winnerName) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.opponent = opponent;
        this.crate = crate;
        this.selfTarget = selfTarget;
        this.oppTarget = oppTarget;
        this.selfScore = selfScore;
        this.oppScore = oppScore;
        this.winnerName = winnerName;

        String title = "§0§lCRATE WAR §8» §c" + viewer.getName() + " §7vs §c" + opponent.getName();
        this.inventory = Bukkit.createInventory(this, SIZE, title.length() > 32 ? "§0§lCRATE WAR" : title);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void start() {
        this.buildFrame();

        List<ItemStack> pool = this.buildPool();
        this.topStrip = this.buildStrip(pool, this.oppTarget.getPreviewItem());
        this.bottomStrip = this.buildStrip(pool, this.selfTarget.getPreviewItem());

        this.viewer.openInventory(this.inventory);

        this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, this::tick, 1L, 1L);
    }

    @NotNull
    private List<ItemStack> buildPool() {
        List<ItemStack> pool = new ArrayList<>();
        for (Reward reward : this.crate.getRewards(this.viewer)) {
            ItemStack item = reward.getPreviewItem();
            if (item != null && item.getType() != Material.AIR) {
                pool.add(item);
            }
        }
        if (pool.isEmpty()) pool.add(new ItemStack(Material.PAPER));
        return pool;
    }

    /**
     * Builds a strip of length {@code STEPS + 2*PAD} filled with random pool items, with the
     * target placed exactly where it lands under the pointer on the final step.
     */
    @NotNull
    private ItemStack[] buildStrip(@NotNull List<ItemStack> pool, @NotNull ItemStack target) {
        int length = STEPS + PAD * 2;
        ItemStack[] strip = new ItemStack[length];
        for (int i = 0; i < length; i++) {
            strip[i] = pool.get(Rnd.get(pool.size())).clone();
        }
        // Final step index is STEPS-1; the visible center is offset by PAD.
        strip[(STEPS - 1) + PAD] = target.clone();
        return strip;
    }

    private void tick() {
        // Stop if the viewer closed the battle view.
        if (!this.viewer.isOnline() || this.viewer.getOpenInventory().getTopInventory() != this.inventory) {
            this.cancel();
            return;
        }

        double progress = Math.min(1.0D, (double) this.elapsed / (double) DURATION_TICKS);
        // easeOutCubic for a natural deceleration.
        double eased = 1.0D - Math.pow(1.0D - progress, 3);
        int index = (int) Math.round(eased * (STEPS - 1));

        if (index != this.lastIndex) {
            this.renderStrip(TOP_SLOTS, this.topStrip, index);
            this.renderStrip(BOTTOM_SLOTS, this.bottomStrip, index);
            this.viewer.playSound(this.viewer.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.6f);
            this.lastIndex = index;
        }

        if (this.elapsed >= DURATION_TICKS) {
            this.finish();
            this.cancel();
            return;
        }

        this.elapsed++;
    }

    private void renderStrip(int[] slots, @NotNull ItemStack[] strip, int index) {
        for (int i = 0; i < slots.length; i++) {
            int stripIndex = index + i;
            ItemStack item = stripIndex < strip.length ? strip[stripIndex] : null;
            this.inventory.setItem(slots[i], item);
        }
    }

    private void finish() {
        boolean draw = this.winnerName == null;
        boolean won = !draw && this.winnerName.equals(this.viewer.getName());

        ItemStack status;
        if (draw) {
            status = named(Material.PAPER, "§e§lDRAW");
        }
        else if (won) {
            status = named(Material.NETHER_STAR, "§a§lYOU WON!");
            this.viewer.playSound(this.viewer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }
        else {
            status = named(Material.BARRIER, "§c§lYOU LOST");
            this.viewer.playSound(this.viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
        this.inventory.setItem(22, status);
        this.inventory.setItem(31, status.clone());
    }

    private void buildFrame() {
        ItemStack border = named(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int slot = 0; slot < SIZE; slot++) {
            this.inventory.setItem(slot, border.clone());
        }

        // Pointers framing each strip's center column.
        ItemStack pointer = named(Material.HOPPER, "§e▼");
        this.inventory.setItem(4, pointer.clone());   // above top strip
        this.inventory.setItem(22, pointer.clone());  // below top strip
        this.inventory.setItem(31, pointer.clone());  // above bottom strip
        this.inventory.setItem(49, pointer.clone());  // below bottom strip

        // Player heads + scores.
        this.inventory.setItem(0, this.head(this.opponent, "§c" + this.opponent.getName(), this.oppScore));
        this.inventory.setItem(45, this.head(this.viewer, "§a" + this.viewer.getName() + " §7(Ty)", this.selfScore));

        // Clear strip rows so the border doesn't flash before the first frame.
        for (int slot : TOP_SLOTS) this.inventory.setItem(slot, null);
        for (int slot : BOTTOM_SLOTS) this.inventory.setItem(slot, null);
    }

    @NotNull
    private ItemStack head(@NotNull Player player, @NotNull String name, double score) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.editMeta(item, meta -> {
            if (meta instanceof SkullMeta skull) skull.setOwningPlayer(player);
            ItemUtil.setCustomName(meta, name);
            ItemUtil.setLore(meta, List.of("§7Wynik: §e" + (long) score + " pkt"));
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
