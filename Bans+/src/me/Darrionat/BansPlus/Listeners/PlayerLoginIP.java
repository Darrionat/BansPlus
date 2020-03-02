package me.Darrionat.BansPlus.Listeners;

import java.net.InetAddress;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Files.FileManager;
import me.Darrionat.BansPlus.Handlers.IPBans.DatabaseIPBansManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class PlayerLoginIP implements Listener {

	private Main plugin;

	public PlayerLoginIP(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent e) {
		InetAddress ip = e.getAddress();
		String ipstr = ip.toString().replace("/", "");

		if (plugin.mysqlEnabled) {
			DatabaseIPBansManager dbIPManager = new DatabaseIPBansManager(plugin);
			if (!dbIPManager.ipExists(ipstr)) {
				return;
			}
		} else {
			FileManager fileManager = new FileManager(plugin);
			FileConfiguration bannedIPsConfig = fileManager.getDataConfig("bannedips");
			ipstr = ipstr.replace(".", "-");
			if (bannedIPsConfig.getConfigurationSection(ipstr) == null) {
				return;
			}

		}

		e.setKickMessage(Utils.chat(Utils.IPbanMessage(plugin, ipstr)));
		// Messages change with ban amount.
		e.setResult(Result.KICK_BANNED);

	}
}
