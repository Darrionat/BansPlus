package me.Darrionat.BansPlus.Handlers.IPBans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		fileManager.saveConfigFile("bannedips", bPlayersConfig);
		return;
	}

	public String getInfo(String ip, String key) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bIPsConfig = fileManager.getDataConfig("bannedips");
		// Banned By, Start, Reason
		String info = bIPsConfig.getString(ip + "." + key);
		return info;
	}

	public List<String> getList() {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration bIPsConfig = fileManager.getDataConfig("bannedips");
		List<String> list = new ArrayList<String>();
		for (String key : bIPsConfig.getKeys(false)) {
			key = key.replace("-", ".");
			list.add(key);
		}
		return list;
	}

}
