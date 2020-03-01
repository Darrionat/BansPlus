package me.Darrionat.BansPlus.Handlers;

import java.util.Date;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Files.FileManager;

public class ConfigIPBansManager {
	private Main plugin;

	public ConfigIPBansManager(Main plugin) {
		this.plugin = plugin;
	}

	public void createIP(String ip, Date startDate, String reason, String bannedBy) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bIPsConfig = fileManager.getDataConfig("bannedips");

		bIPsConfig.createSection(ip);
		ConfigurationSection bPlayerSection = bIPsConfig.getConfigurationSection(ip);
		bPlayerSection.set("Start", startDate.toString());
		bPlayerSection.set("Reason", reason);
		bPlayerSection.set("Banned By", bannedBy);

		fileManager.saveConfigFile("bannedips", bIPsConfig);
	}

	public boolean ipExists(String ip) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bIPsConfig = fileManager.getDataConfig("bannedips");
		if (bIPsConfig.getConfigurationSection(ip) == null) {
			return false;
		}
		return true;
	}

	public void removeIP(String ip) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bPlayersConfig = fileManager.getDataConfig("bannedips");
		bPlayersConfig.set(ip, null);
		return;
	}

	public String getBannedBy(String ip) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bIPsConfig = fileManager.getDataConfig("bannedips");
		String bannedBy = bIPsConfig.getString(ip + ".Banned By");
		return bannedBy;
	}

	public String getStartTime(String ip) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bIPsConfig = fileManager.getDataConfig("bannedips");
		String start = bIPsConfig.getString(ip + ".Start");
		return start;
	}

	public String getReason(String ip) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bIPsConfig = fileManager.getDataConfig("bannedips");
		String reason = bIPsConfig.getString(ip + ".Reason");
		return reason;
	}

}
