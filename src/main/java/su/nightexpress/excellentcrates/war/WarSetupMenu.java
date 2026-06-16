package su.nightexpress.excellentcrates.war;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import static su.nightexpress.excellentcrates.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

/**
 * GUI opened from the crate preview ("graj o wojnę"): the player picks the number of keys to stake
 * and clicks confirm. The plugin then asks them to type the opponent's name in chat
 * ({@link WarManager#promptTarget}). The opponent accepts/denies via command or the clickable chat message.
 */
public class WarSetupMenu extends LinkedMenu<CratesPlugin, WarSetupMenu.Data> implements ConfigBased {

    public record Data(@NotNull Crate crate, int amount) {}

    private int[] decreaseSlots;
    private int[] amountSlots;
    private int[] increaseSlots;
    private int[] confirmSlots;

    public WarSetupMenu(@NotNull CratesPlugin plugin) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap("Crate War: " + CRATE_NAME));
    }

    public void open(@NotNull Player player, @NotNull Crate crate) {
        int min = Config.WAR_MIN_KEYS.get();
        this.open(player, new Data(crate, min));
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        return this.getLink(viewer).crate().replacePlaceholders().apply(super.getTitle(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.addAmountControls(viewer);
        this.addConfirmButton(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    private void addAmountControls(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Data data = this.getLink(viewer);
        Crate crate = data.crate();
        int amount = data.amount();

        int min = Config.WAR_MIN_KEYS.get();
        int max = Config.WAR_MAX_KEYS.get();

        viewer.addItem(NightItem.fromType(Material.RED_DYE)
            .setDisplayName(RED.wrap(BOLD.wrap("- Mniej kluczy")))
            .setLore(Lists.newList(
                GRAY.wrap("Lewy klik: " + WHITE.wrap("-1")),
                GRAY.wrap("Prawy klik: " + WHITE.wrap("-10"))
            ))
            .toMenuItem()
            .setSlots(this.decreaseSlots)
            .setPriority(Integer.MAX_VALUE)
            .setHandler((v, event) -> {
                int step = event.isRightClick() ? 10 : 1;
                int next = Math.max(min, amount - step);
                this.runNextTick(() -> this.open(player, new Data(crate, next)));
            })
            .build()
        );

        viewer.addItem(NightItem.fromType(Material.PAPER)
            .setDisplayName(YELLOW.wrap(BOLD.wrap("Stawka: x" + amount + " kluczy")))
            .setLore(Lists.newList(
                GRAY.wrap("Skrzynia: " + WHITE.wrap(crate.getName())),
                GRAY.wrap("Zakres: " + WHITE.wrap(min + " - " + max))
            ))
            .setAmount(Math.max(1, Math.min(64, amount)))
            .toMenuItem()
            .setSlots(this.amountSlots)
            .setPriority(Integer.MAX_VALUE)
            .build()
        );

        viewer.addItem(NightItem.fromType(Material.LIME_DYE)
            .setDisplayName(GREEN.wrap(BOLD.wrap("+ Więcej kluczy")))
            .setLore(Lists.newList(
                GRAY.wrap("Lewy klik: " + WHITE.wrap("+1")),
                GRAY.wrap("Prawy klik: " + WHITE.wrap("+10"))
            ))
            .toMenuItem()
            .setSlots(this.increaseSlots)
            .setPriority(Integer.MAX_VALUE)
            .setHandler((v, event) -> {
                int step = event.isRightClick() ? 10 : 1;
                int next = Math.min(max, amount + step);
                this.runNextTick(() -> this.open(player, new Data(crate, next)));
            })
            .build()
        );
    }

    private void addConfirmButton(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Data data = this.getLink(viewer);
        Crate crate = data.crate();
        int amount = data.amount();

        viewer.addItem(NightItem.fromType(Material.NETHERITE_SWORD)
            .setDisplayName(RED.wrap(BOLD.wrap("Wyzwij na wojnę")))
            .setLore(Lists.newList(
                GRAY.wrap("Skrzynia: " + WHITE.wrap(crate.getName())),
                GRAY.wrap("Stawka: " + YELLOW.wrap("x" + amount + " kluczy")),
                "",
                GREEN.wrap("» Kliknij, a następnie wpisz na czacie"),
                GREEN.wrap("  nick gracza, którego chcesz wyzwać.")
            ))
            .toMenuItem()
            .setSlots(this.confirmSlots)
            .setPriority(Integer.MAX_VALUE)
            .setHandler((v, event) -> this.runNextTick(() -> {
                player.closeInventory();
                this.plugin.getWarManager().promptTarget(player, crate, amount);
            }))
            .build()
        );
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.decreaseSlots = ConfigValue.create("Amount.Decrease_Slots", new int[]{29}).read(config);
        this.amountSlots = ConfigValue.create("Amount.Display_Slots", new int[]{31}).read(config);
        this.increaseSlots = ConfigValue.create("Amount.Increase_Slots", new int[]{33}).read(config);
        this.confirmSlots = ConfigValue.create("Confirm_Slots", new int[]{22}).read(config);

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28,30,32,34,35,37,38,39,40,41,42,43,44)
        );

        loader.addDefaultItem(MenuItem.buildExit(this, 36).setPriority(10));
    }
}
