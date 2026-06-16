package su.nightexpress.excellentcrates.war;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.nightcore.manager.AbstractListener;

public class WarListener extends AbstractListener<CratesPlugin> {

    private final WarManager manager;

    public WarListener(@NotNull CratesPlugin plugin, @NotNull WarManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatTargetInput(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!this.manager.isAwaitingTarget(player)) return;

        // Consume the typed opponent name so it isn't broadcast to global chat.
        if (this.manager.handleTargetInput(player, event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
