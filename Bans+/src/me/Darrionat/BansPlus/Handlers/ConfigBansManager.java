package me.Darrionat.BansPlus.Handlers;

import java.util.Date;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Files.FileManager;

public class ConfigBansManager {
	private Main plugin;

	public ConfigBansManager(Main plugin) {
		this.plugin = plugin;
	}

	public void useConfig(OfflinePlayer bPlayer, Date startDate, Date endDate, String reason, String bannedBy) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		String bPlayerUUID = bPlayer.getUniqueId().toString();

		bPlayersConfig.createSection(bPlayerUUID);
		ConfigurationSection bPlayerSection = bPlayersConfig.getConfigurationSection(bPlayerUUID);
		bPlayerSection.set("Start", startDate.toString());
		if (endDate == null) {
			bPlayerSection.set("End", "Permanent");
		} else {
			bPlayerSection.set("End", endDate.toString());
		}

		bPlayerSection.set("Reason", reason.toString());
		bPlayerSection.set("Banned By", bannedBy);

		fileManager.saveConfigFile("bannedplayers", bPlayersConfig);
	}

	public boolean playerExists(String uuid) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		if (bPlayersConfig.getConfigurationSection(uuid) == null) {
			return false;
		}
		return true;
	}

	public void removePlayer(String uuid) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		bPlayersConfig.set(uuid, null);
		return;
	}

	public String getBannedBy(String uuid) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		String bannedBy = bPlayersConfig.getString(uuid + ".Banned By");
		return bannedBy;
	}

	public String getStartTime(String uuid) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		String start = bPlayersConfig.getString(uuid + ".Start");
		return start;
	}

	public String getEndTime(String uuid) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		String end = bPlayersConfig.getString(uuid + ".End");
		return end;
	}

	public String getReason(String uuid) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		String reason = bPlayersConfig.getString(uuid + ".Reason");
		return reason;
	}

}
