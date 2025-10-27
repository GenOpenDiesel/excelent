package su.nightexpress.excellentcrates.crate.menu;

import org.bukkit.Material;
import org.bukkit.Sound; // Added import for Sound
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.Placeholders; // Added import for Placeholders
import su.nightexpress.excellentcrates.api.crate.Reward;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Lang; // Added import for Lang
import su.nightexpress.excellentcrates.config.Perms;
import su.nightexpress.excellentcrates.crate.cost.Cost;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.CrateSource;
import su.nightexpress.excellentcrates.crate.impl.OpenOptions;
import su.nightexpress.excellentcrates.util.InteractType;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.ItemHandler;
import su.nightexpress.nightcore.ui.menu.item.ItemOptions;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional; // Added import for Optional
import java.util.stream.Collectors; // Added import for Collectors

import static su.nightexpress.excellentcrates.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class PreviewMenu extends LinkedMenu<CratesPlugin, CrateSource> implements Filled<Reward>, ConfigBased {

    private static final String NO_PERMISSION = "%no_permission%";

    private int[]        rewardSlots;
    private String       rewardName;
    private List<String> rewardLore;
    private List<String> noPermissionLore;
    private List<String> limitsLore;
    private boolean      hideUnavailable;

    public PreviewMenu(@NotNull CratesPlugin plugin, @NotNull FileConfig config) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap(CRATE_NAME));
        this.setApplyPlaceholderAPI(true);
        this.load(config);
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        CrateSource source = this.getLink(viewer);

        return source.getCrate().replacePlaceholders().apply(this.title);
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    public MenuFiller<Reward> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Crate crate = this.getLink(player).getCrate();

        var autoFill = MenuFiller.builder(this);

        autoFill.setSlots(this.rewardSlots);
        autoFill.setItems((this.hideUnavailable ? crate.getRewards(player) : crate.getRewards()).stream().filter(Reward::isRollable).toList());
        autoFill.setItemCreator(reward -> {
            List<String> restrictions = new ArrayList<>();
            List<String> limits = new ArrayList<>();

            if (reward.fitRequirements(player)) {
                if (reward.getLimits().isEnabled() && reward.getLimits().isAmountLimited()) {
                    limits.addAll(Replacer.create()
                            .replace(GENERIC_AMOUNT, () -> String.valueOf(reward.getAvailableRolls(player)))
                            .apply(this.limitsLore)
                    );
                }
            }
            else {
                restrictions.addAll(this.noPermissionLore);
            }

            return NightItem.fromItemStack(reward.getPreviewItem())
                    .ignoreNameAndLore()
                    .setDisplayName(this.rewardName)
                    .setLore(this.rewardLore)
                    .replacement(replacer -> {
                                replacer
                                        .replace(GENERIC_LIMITS, limits)
                                        .replace(NO_PERMISSION, restrictions)
                                        .replace("%win_limit_amount%", limits) // Keep legacy placeholders for compatibility if needed
                                        .replace("%win_limit_cooldown%", Collections.emptyList()) // Keep legacy placeholders
                                        .replace("%win_limit_drained%", Collections.emptyList())  // Keep legacy placeholders
                                        .replace("%win_limit_no_permission%", restrictions) // Keep legacy placeholders
                                        .replace(reward.replacePlaceholders())
                                        .replace(crate.replacePlaceholders());
                                if (this.applyPlaceholderAPI) {
                                    replacer.replacePlaceholderAPI(player);
                                }
                            }
                    );
        });

        return autoFill.build();
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.hideUnavailable = ConfigValue.create("Reward.Hide_Unavailable",
                true,
                "When enabled, displays only rewards that can be rolled out for a player."
        ).read(config);

        this.rewardSlots = ConfigValue.create("Reward.Slots",
                new int[] {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34}
        ).read(config);

        this.rewardName = ConfigValue.create("Reward.Name",
                REWARD_NAME
        ).read(config);

        this.rewardLore = ConfigValue.create("Reward.Lore.Default", Lists.newList(
                NO_PERMISSION,
                EMPTY_IF_ABOVE,
                DARK_GRAY.wrap("»") + GRAY.wrap( " Rarity: " + WHITE.wrap(REWARD_RARITY_NAME) + " → " + GREEN.wrap(REWARD_ROLL_CHANCE + "%")),
                GENERIC_LIMITS,
                EMPTY_IF_BELOW,
                REWARD_DESCRIPTION
        )).read(config);

        this.noPermissionLore = ConfigValue.create("Reward.Lore.No_Permission", Lists.newList(
                GRAY.wrap(RED.wrap("✘") + " You don't have access to this reward.")
        )).read(config);

        // Retained legacy 'LimitInfo' for backward compatibility, but favor 'Limits.Info'
        List<String> defaultLimitsLore = ConfigValue.create("Reward.Lore.LimitInfo", Lists.newList(
                DARK_GRAY.wrap("»") + GRAY.wrap(" Rolls Available: ") + YELLOW.wrap(GENERIC_AMOUNT)
        )).read(config);
        this.limitsLore = ConfigValue.create("Reward.Lore.Limits.Info", defaultLimitsLore).read(config);


        // --- Item Handlers ---

        loader.addHandler(new ItemHandler("open", (viewer, event) -> {
            CrateSource source = this.getLink(viewer);
            // Updated check: Ensure crate source is valid before attempting to open.
            if (!source.hasItem() && !source.hasBlock()) return; // Check if source is item OR block

            Player player = viewer.getPlayer();

            this.runNextTick(() -> {
                player.closeInventory();
                // Ensure correct InteractType is passed
                plugin.getCrateManager().interactCrate(player, source.getCrate(), InteractType.CRATE_OPEN, source.getItem(), source.getBlock());
            });
        }, ItemOptions.builder().setVisibilityPolicy(viewer -> {
            CrateSource source = this.getLink(viewer);
            // Visibility check: show if source is item OR block
            return source.hasItem() || source.hasBlock();
        }).build()));

        // *** Add the mass_open handler ***
        loader.addHandler(new ItemHandler("mass_open", (viewer, event) -> {
            CrateSource source = this.getLink(viewer);
            Player player = viewer.getPlayer();
            Crate crate = source.getCrate();

            // *** Check affordability ***
            Optional<Cost> firstAffordableCostOpt = crate.getCosts().stream()
                    .filter(Cost::isAvailable)
                    .filter(cost -> cost.canAfford(player))
                    .findFirst();

            Cost costToUse = firstAffordableCostOpt.orElse(null); // Get the cost or null if none affordable/available

            // If the crate has costs AND the player cannot afford *any* of them
            if (crate.hasCost() && costToUse == null) {
                // Find the first defined cost to display in the message, even if unaffordable
                Cost firstCost = crate.getFirstCost().orElse(null);
                if (firstCost != null) {
                    Lang.CRATE_OPEN_TOO_EXPENSIVE.message().send(player, replacer -> replacer
                            .replace(crate.replacePlaceholders())
                            .replace(Placeholders.GENERIC_COSTS, () -> firstCost.formatInline(", ")) // Show the first cost
                    );
                } else {
                     // Fallback if somehow no costs are defined but hasCost() was true (shouldn't happen)
                     player.sendMessage("You cannot afford to open this crate."); // Consider adding a Lang entry
                }
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f); // Play feedback sound
                return; // Stop execution
            }
            // *** End affordability check ***

            // Determine max openings based on the affordable cost or default limit
            int maxOpenings = (costToUse != null)
                    ? costToUse.countMaxOpenings(player)
                    : Config.MASS_OPENING_LIMIT.get(); // If free, use config limit

            int amountToOpen = Math.min(maxOpenings, Config.MASS_OPENING_LIMIT.get()); // Ensure it doesn't exceed global limit

            if (amountToOpen <= 0) {
                 // Send a message if they can afford but the calculated amount is somehow zero
                 player.sendMessage("You don't have enough resources to open any crates."); // Consider adding a Lang entry
                 player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                 return; // Stop execution
            }

            this.runNextTick(() -> {
                player.closeInventory();
                plugin.getCrateManager().multiOpenCrate(player, source, OpenOptions.empty(), costToUse, amountToOpen); // Use the determined cost
            });
        }, ItemOptions.builder().setVisibilityPolicy(viewer -> {
             // Show mass open button only if the feature is enabled and the player has permission
             Player player = viewer.getPlayer();
             return Config.isMassOpenEnabled() && player.hasPermission(Perms.MASS_OPEN); // Use Perms.MASS_OPEN
        }).build()));
        // *** End of mass_open handler addition ***


        // --- Default Items --- (Load these after handlers)

        loader.addDefaultItem(new NightItem(Material.BLACK_STAINED_GLASS_PANE).setHideTooltip(true).toMenuItem()
                .setSlots(1,2,3,5,6,7,9,18,27,17,26,35,37,38,39,40,41,42,43));

        loader.addDefaultItem(new NightItem(Material.GRAY_STAINED_GLASS_PANE).setHideTooltip(true).toMenuItem()
                .setSlots(0,4,8,36,44));

        loader.addDefaultItem(NightItem.asCustomHead("1daf09284530ce92ed2df2a62e1b05a11f1871f85ae559042844206d66c0b5b0")
                .setDisplayName(GOLD.wrap(BOLD.wrap("Milestones")))
                .toMenuItem()
                .setPriority(10)
                .setSlots(4)
                .setHandler(new ItemHandler("milestones", (viewer, event) -> {
                    this.runNextTick(() -> plugin.getCrateManager().openMilestones(viewer.getPlayer(), this.getLink(viewer)));
                }, ItemOptions.builder().setVisibilityPolicy(viewer -> this.getLink(viewer).getCrate().hasMilestones()).build()))
        );

        loader.addDefaultItem(MenuItem.buildExit(this, 40).setPriority(10));
        loader.addDefaultItem(MenuItem.buildNextPage(this, 26).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 18).setPriority(10));
    }
}
