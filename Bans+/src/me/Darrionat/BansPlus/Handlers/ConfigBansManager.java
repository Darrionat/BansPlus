package me.Darrionat.BansPlus.Handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	public void useConfig(OfflinePlayer bPlayer, Date startDate, Date endDate, String reason, String username,
			String bannedBy) {
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
		bPlayerSection.set("Username", username);
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

	public String getInfo(String uuid, String key) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		// Banned By, Start, End, Reason
		String reason = bPlayersConfig.getString(uuid + "." + key);
		return reason;
	}

	public List<String> getList() {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedplayers");
		List<String> list = new ArrayList<String>();
		for (String key : bPlayersConfig.getKeys(false)) {
			String name = getInfo(key, "Username");
			list.add(name);
		}
		return list;
	}

}
