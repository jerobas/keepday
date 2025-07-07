package com.flaviozno.keepday;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class KeepDay extends JavaPlugin {

    private final HashMap<UUID, Boolean> showingDayProgress = new HashMap<>();

    @Override
    public void onEnable() {
        getCommand("keepdaytoggle").setExecutor(new ToggleCommand());

        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                   long time = world.getTime();
                    if (time >= 13000  && time <= 13002) {
                        world.setTime(0);
                    }
                }

                for (UUID uuid : showingDayProgress.keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        long day = player.getWorld().getFullTime() / 24000 + 1;
                        double progress = (player.getWorld().getTime() % 24000) / 24000.0;
                        int percentage = (int) (progress * 100);
                        player.sendMessage(ChatColor.GOLD + "Day " + day + " progress: " + percentage + "%");
                    }
                }
            }
        }.runTaskTimer(this, 0L, 40L);
    }

    class ToggleCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Players only.");
                return true;
            }
            UUID uuid = player.getUniqueId();

            if (showingDayProgress.containsKey(uuid)) {
                showingDayProgress.remove(uuid);
                player.sendMessage(ChatColor.RED + "Day progress messages disabled.");
            } else {
                showingDayProgress.put(uuid, true);
                player.sendMessage(ChatColor.GREEN + "Day progress messages enabled.");
            }
            return true;
        }
    }
}
