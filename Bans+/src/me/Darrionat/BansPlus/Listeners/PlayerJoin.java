package me.Darrionat.BansPlus.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Utils.UpdateChecker;
import me.Darrionat.BansPlus.Utils.Utils;

public class PlayerJoin implements Listener {

	private Main plugin;

	public PlayerJoin(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (plugin.getConfig().getBoolean("checkUpdates") != true) {
			return;
		}
		UpdateChecker updater = new UpdateChecker(plugin, 76083);
		try {
			if (updater.checkForUpdates()) {
				Player p = e.getPlayer();
				if (!p.isOp()) {
					return;
				}
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

					public void run() {
						String version = "v" + plugin.getDescription().getVersion();
						String latestVersion = UpdateChecker.getLatestVersion();
						p.sendMessage(Utils.chat("&eUpdate available! &bBans+ is currently on " + version));
						p.sendMessage(Utils.chat("&bDownload version " + latestVersion + " here!"));
						p.sendMessage(Utils.chat("https://www.spigotmc.org/resources/bans.76083/"));
					}
				}, 30L);// 60 L == 3 sec, 20 ticks == 1 sec

			}
		} catch (Exception exe) {
			plugin.getLogger().info("Could not check for updates! Stacktrace:");
			exe.printStackTrace();
		}

	}
}