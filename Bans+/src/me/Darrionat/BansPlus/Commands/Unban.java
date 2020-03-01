package me.Darrionat.BansPlus.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Handlers.ConfigBansManager;
import me.Darrionat.BansPlus.Handlers.ConfigIPBansManager;
import me.Darrionat.BansPlus.Handlers.DatabaseBansManager;
import me.Darrionat.BansPlus.Handlers.DatabaseIPBansManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class Unban implements CommandExecutor {
	private Main plugin;

	public Unban(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("unban").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		ConfigBansManager confManager = new ConfigBansManager(plugin);
		DatabaseBansManager dbManager = new DatabaseBansManager(plugin);
		ConfigIPBansManager confIPManager = new ConfigIPBansManager(plugin);
		DatabaseIPBansManager dbIPManager = new DatabaseIPBansManager(plugin);
		String unbanMsg = Utils.chat(config.getString("Messages.Unban Successful").replace("%player%", args[0]));

		String ip = args[0];
		if (confIPManager.ipExists(ip)) {
			confIPManager.removeIP(ip);
			sender.sendMessage(unbanMsg);
			return true;
		}
		if (dbIPManager.ipExists(ip)) {
			dbIPManager.removeIP(ip);
			sender.sendMessage(unbanMsg);
			return true;
		}

		@SuppressWarnings("deprecation")
		OfflinePlayer bPlayer = Bukkit.getOfflinePlayer(args[0]);
		String uuid = bPlayer.getUniqueId().toString();
		if (confManager.playerExists(uuid)) {
			confManager.removePlayer(uuid);
			sender.sendMessage(unbanMsg);
			return true;
		}
		if (dbManager.playerExists(uuid)) {
			dbManager.removePlayer(uuid);
			sender.sendMessage(unbanMsg);
			return true;
		}
		// Not an IP or player that's banned
		sender.sendMessage(Utils.chat(config.getString("Messages.Unban DNE")));
		return true;
	}

}
