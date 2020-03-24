package me.Darrionat.BansPlus.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Handlers.Mutes.ConfigMutesManager;
import me.Darrionat.BansPlus.Handlers.Mutes.DatabaseMutesManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class MuteInfo implements CommandExecutor {
	private Main plugin;

	public MuteInfo(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("muteinfo").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.muteinfo";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length == 0) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/muteinfo [player]"));
			return true;
		}
		if (Bukkit.getOfflinePlayer(args[0]) == null) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Invalid Player").replace("%name%", args[0])));
			return true;
		}
		OfflinePlayer bPlayer = Bukkit.getOfflinePlayer(args[0]);
		String uuidStr = bPlayer.getUniqueId().toString();
		if (plugin.mysqlEnabled) {
			DatabaseMutesManager dbManager = new DatabaseMutesManager(plugin);
			if (dbManager.playerExists(uuidStr)) {
				sendMuteInfo(sender, uuidStr);
				return true;
			}
			sender.sendMessage(Utils.chat(config.getString("Messages.Player Not Banned")));
			return true;
		}
		// MySQL not enabled
		ConfigMutesManager confManager = new ConfigMutesManager(plugin);
		if (confManager.playerExists(uuidStr)) {
			sendMuteInfo(sender, uuidStr);
			return true;
		}
		sender.sendMessage(Utils.chat(config.getString("Messages.Unmute DNE")));
		return true;

	}

	public void sendMuteInfo(CommandSender sender, String uuidStr) {
		String username = "&6Username: &f%username%";
		String uuid = "&6UUID: &F" + uuidStr;
		String startTime = "&6Start: &f%startTime%";
		String endTime = "&6End: &f%endTime%";
		String reason = "&6Reason: &f%reason%";
		String mutedBy = "&6Banned By: &f%mutedBy%";

		if (plugin.mysqlEnabled) {
			DatabaseMutesManager dbManager = new DatabaseMutesManager(plugin);
			username = username.replace("%username%", dbManager.getInfo(uuidStr, "USERNAME"));
			startTime = startTime.replace("%startTime%", dbManager.getInfo(uuidStr, "START"));
			endTime = endTime.replace("%endTime%", dbManager.getInfo(uuidStr, "END"));
			reason = reason.replace("%reason%", dbManager.getInfo(uuidStr, "REASON"));
			mutedBy = mutedBy.replace("%mutedBy%", dbManager.getInfo(uuidStr, "MUTEDBY"));

		} else {
			ConfigMutesManager confManager = new ConfigMutesManager(plugin);
			username = username.replace("%username%", confManager.getInfo(uuidStr, "Username"));
			startTime = startTime.replace("%startTime%", confManager.getInfo(uuidStr, "Start"));
			endTime = endTime.replace("%endTime%", confManager.getInfo(uuidStr, "End"));
			reason = reason.replace("%reason%", confManager.getInfo(uuidStr, "Reason"));
			mutedBy = mutedBy.replace("%mutedBy%", confManager.getInfo(uuidStr, "Muted By"));
		}
		sender.sendMessage(Utils.chat(username));
		sender.sendMessage(Utils.chat(uuid));
		sender.sendMessage(Utils.chat(startTime));
		sender.sendMessage(Utils.chat(endTime));
		sender.sendMessage(Utils.chat(reason));
		sender.sendMessage(Utils.chat(mutedBy));
	}

}
