package me.Darrionat.BansPlus.Listeners;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Files.FileManager;
import me.Darrionat.BansPlus.Handlers.Mutes.DatabaseMutesManager;

public class AsyncPlayerChat implements Listener {
	private Main plugin;

	public AsyncPlayerChat(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private Date endDate;
	private long diff;

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String uuidString = uuid.toString();
		String endDateString = "";
		if (plugin.mysqlEnabled) {
			DatabaseMutesManager dbMutesManager = new DatabaseMutesManager(plugin);
			if (!dbMutesManager.playerExists(uuid.toString())) {
				return;
			}
			endDateString = dbMutesManager.getInfo(uuidString, "END");

		} else {
			FileManager fileManager = new FileManager(plugin);
			FileConfiguration mPlayersConfig = fileManager.getDataConfig("mutedplayers");
			if (mPlayersConfig.getConfigurationSection(uuidString) == null) {
				return;
			}
			ConfigurationSection mPlayerSection = mPlayersConfig.getConfigurationSection(uuidString);
			endDateString = mPlayerSection.getString("End");
		}
		try {
			endDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(endDateString);
		} catch (ParseException exe) {
			exe.printStackTrace();
			return;
		}

		diff = endDate.getTime() - System.currentTimeMillis();
		if (diff < 0) {
			return;
		}
		muted();
		e.setCancelled(true);
	}

	public void muted() {
		diff = diff / 1000; // Milliseconds to seconds
		long hours = diff / 3600;
		long minutes = (diff % 3600) / 60;
		long seconds = diff % 60;
		String timeLeft = String.format("%02dh %02dm %02ds", hours, minutes, seconds);

	}
}
