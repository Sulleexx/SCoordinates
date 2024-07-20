package sullexxx.scoordinates;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class SCoordinates extends JavaPlugin implements Listener {
    private static final LegacyComponentSerializer unusualHexSerializer = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (getConfig().getBoolean("Hide-Coordinates")) {
            for (World world : Bukkit.getWorlds()) {
                world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
            }
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SCoordinates enabled and ready to work.");
    }


    @Override
    public void onDisable() {
        getLogger().info("SCoordinates disabled.");
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Material material = Material.valueOf(getConfig().getString("Coordinate-item.Material"));
        if (!getConfig().getBoolean("Coordinate-item.Enable")) return;
        if (player.getInventory().getItem(event.getNewSlot()) != null &&
                player.getInventory().getItem(event.getNewSlot()).getType() == material) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getInventory().getItemInMainHand().getType() == material ||
                            player.getInventory().getItemInOffHand().getType() == material) {
                        Location loc = player.getLocation();
                        String XYZ = getConfig().getString("Coordinate-ActionBar.format").replace("{X}", String.valueOf(loc.getBlockX())).replace("{Y}", String.valueOf(loc.getBlockY())).replace("{Z}", String.valueOf(loc.getBlockZ()));
                        player.sendActionBar(doubleFormat(XYZ));
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(this, 0L, 2L);
        }
    }

    @NotNull
    public static Component doubleFormat(@NotNull String message) {
        message = message.replace('§', '&');
        Component component = MiniMessage.miniMessage().deserialize(message).decoration(TextDecoration.ITALIC, false);
        String legacyMessage = toLegacy(component);
        legacyMessage = ChatColor.translateAlternateColorCodes('&', legacyMessage);
        return unusualHexSerializer.deserialize(legacyMessage);
    }

    public static String toLegacy(Component component) {
        return unusualHexSerializer.serialize(component);
    }
}
