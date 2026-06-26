package su.nightexpress.excellentcrates.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public final class FoliaTasks {

    private static final boolean FOLIA = isClassPresent("io.papermc.paper.threadedregions.RegionizedServer");

    private FoliaTasks() {
    }

    @NotNull
    public static Scheduled runAtFixedRate(@NotNull Plugin plugin,
                                           @NotNull Entity entity,
                                           @NotNull Runnable runnable,
                                           @Nullable Runnable retired,
                                           long delayTicks,
                                           long periodTicks) {
        if (!FOLIA) {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks);
            return task::cancel;
        }

        Object task = invokeEntityScheduler(plugin, entity, "runAtFixedRate", runnable, retired, Math.max(1L, delayTicks), Math.max(1L, periodTicks));
        return () -> cancel(task);
    }

    @NotNull
    public static Scheduled runDelayed(@NotNull Plugin plugin,
                                       @NotNull Entity entity,
                                       @NotNull Runnable runnable,
                                       @Nullable Runnable retired,
                                       long delayTicks) {
        if (!FOLIA) {
            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
            return task::cancel;
        }

        Object task = invokeEntityScheduler(plugin, entity, "runDelayed", runnable, retired, Math.max(1L, delayTicks));
        return () -> cancel(task);
    }

    @SuppressWarnings("unchecked")
    private static Object invokeEntityScheduler(@NotNull Plugin plugin,
                                                @NotNull Entity entity,
                                                @NotNull String methodName,
                                                @NotNull Runnable runnable,
                                                @Nullable Runnable retired,
                                                long... ticks) {
        try {
            Object scheduler = entity.getClass().getMethod("getScheduler").invoke(entity);
            Class<?>[] signature = ticks.length == 1
                ? new Class<?>[]{Plugin.class, Consumer.class, Runnable.class, long.class}
                : new Class<?>[]{Plugin.class, Consumer.class, Runnable.class, long.class, long.class};
            Method method = scheduler.getClass().getMethod(methodName, signature);

            Object[] arguments = ticks.length == 1
                ? new Object[]{plugin, (Consumer<Object>) task -> runnable.run(), retired, ticks[0]}
                : new Object[]{plugin, (Consumer<Object>) task -> runnable.run(), retired, ticks[0], ticks[1]};
            return method.invoke(scheduler, arguments);
        }
        catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not schedule Folia entity task.", exception);
        }
    }

    private static void cancel(@Nullable Object task) {
        if (task == null) return;

        try {
            task.getClass().getMethod("cancel").invoke(task);
        }
        catch (ReflectiveOperationException ignored) {
            // Nothing actionable: the task is already gone or the platform changed its wrapper.
        }
    }

    private static boolean isClassPresent(@NotNull String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException exception) {
            return false;
        }
    }

    public interface Scheduled {
        void cancel();
    }
}
