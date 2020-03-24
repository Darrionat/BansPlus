package me.Darrionat.BansPlus.Handlers.Mutes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Files.FileManager;

public class ConfigMutesManager {
	private Main plugin;

	public ConfigMutesManager(Main plugin) {
		this.plugin = plugin;
	}

	public void useConfig(OfflinePlayer bPlayer, Date startDate, Date endDate, String reason, String username,
			String mutedBy) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration mutedPlayersConfig = fileManager.getDataConfig("mutedplayers");
		String bPlayerUUID = bPlayer.getUniqueId().toString();

		mutedPlayersConfig.createSection(bPlayerUUID);
		ConfigurationSection bPlayerSection = mutedPlayersConfig.getConfigurationSection(bPlayerUUID);
		bPlayerSection.set("Start", startDate.toString());
		if (endDate == null) {
			bPlayerSection.set("End", "Permanent");
		} else {
			bPlayerSection.set("End", endDate.toString());
		}

		bPlayerSection.set("Reason", reason.toString());
		bPlayerSection.set("Username", username);
		bPlayerSection.set("Muted By", mutedBy);

		fileManager.saveConfigFile("mutedplayers", mutedPlayersConfig);
	}

	public boolean playerExists(String uuid) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration mutedPlayersConfig = fileManager.getDataConfig("mutedplayers");
		if (mutedPlayersConfig.getConfigurationSection(uuid) == null) {
			return false;
		}
		return true;
	}

	public void removePlayer(String uuid) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration mutedPlayersConfig = fileManager.getDataConfig("mutedplayers");
		mutedPlayersConfig.set(uuid, null);
		fileManager.saveConfigFile("mutedplayers", mutedPlayersConfig);
		return;
	}

	public String getInfo(String uuid, String key) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration mutedPlayersConfig = fileManager.getDataConfig("mutedplayers");
		// Muted By, Start, End, Reason
		String reason = mutedPlayersConfig.getString(uuid + "." + key);
		return reason;
	}

	public List<String> getList() {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration mutedPlayersConfig = fileManager.getDataConfig("mutedplayers");
		List<String> list = new ArrayList<String>();
		for (String key : mutedPlayersConfig.getKeys(false)) {
			String name = getInfo(key, "Username");
			list.add(name);
		}
		return list;
	}

}
