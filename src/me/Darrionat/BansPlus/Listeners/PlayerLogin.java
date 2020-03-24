package me.Darrionat.BansPlus.Listeners;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Files.FileManager;
import me.Darrionat.BansPlus.Handlers.Bans.DatabaseBansManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class PlayerLogin implements Listener {

	private Main plugin;

	public PlayerLogin(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Date endDate;

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		String uuidString = uuid.toString();
		String endDateString = "";
		DatabaseBansManager dbManager = new DatabaseBansManager(plugin);
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		if (plugin.mysqlEnabled) {

			if (!dbManager.playerExists(uuid.toString())) {
				return;
			}
			if (dbManager.getInfo(uuidString, "END").equalsIgnoreCase("Permanent")) {
				e.setKickMessage(Utils.chat(Utils.banMessage(plugin, uuidString))); // Messages change with ban amount.
				e.setResult(Result.KICK_BANNED);
				return;
			}
			endDateString = dbManager.getInfo(uuidString, "END");

		} else {

			if (bPlayersConfig.getConfigurationSection(uuidString) == null) {
				return;
			}
			ConfigurationSection bPlayerSection = bPlayersConfig.getConfigurationSection(uuidString);
			if (bPlayerSection.getString("End").equalsIgnoreCase("Permanent")) {
				e.setKickMessage(Utils.chat(Utils.banMessage(plugin, uuidString))); // Messages change with ban amount.
				e.setResult(Result.KICK_BANNED);
				return;
			}

			// Check if the ban is past that date
			// If it isn't a perm ban. Get the difference
			endDateString = bPlayerSection.getString("End");
		}
		try {
			endDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(endDateString);
		} catch (ParseException exe) {
			exe.printStackTrace();
			return;
		}

		long diff = endDate.getTime() - System.currentTimeMillis();
		if (diff < 0) {
			if (plugin.mysqlEnabled) {
				dbManager.removePlayer(uuidString);
				return;
			}
			bPlayersConfig.set(uuidString, null);
			fileManager.saveConfigFile("bannedplayers", bPlayersConfig);
			return;
		}

		e.setKickMessage(Utils.chat(Utils.banMessage(plugin, uuidString))); // Messages change with ban amount.
		e.setResult(Result.KICK_BANNED);

	}
}
