package me.Darrionat.BansPlus.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Handlers.IPBans.ConfigIPBansManager;
import me.Darrionat.BansPlus.Handlers.IPBans.DatabaseIPBansManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class IPBanInfo implements CommandExecutor {
	private Main plugin;

	public IPBanInfo(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("ipbaninfo").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.ipbaninfo";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length == 0) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/ipbaninfo [ip]"));
			return true;
		}
		if (plugin.mysqlEnabled) {
			DatabaseIPBansManager dbManager = new DatabaseIPBansManager(plugin);
			if (dbManager.ipExists(args[0])) {
				sendBanInfo(sender, args[0]);
				return true;
			}
			sender.sendMessage(Utils.chat(config.getString("Messages.Unban DNE")));
			return true;
		}
		// MySQL not enabled
		ConfigIPBansManager confManager = new ConfigIPBansManager(plugin);
		if (confManager.ipExists(args[0])) {
			sendBanInfo(sender, args[0]);
			return true;
		}
		sender.sendMessage(Utils.chat(config.getString("Messages.Unban DNE")));
		return true;

	}

	public void sendBanInfo(CommandSender sender, String ipStr) {
		String ip = "&6IP: &F" + ipStr;
		String startTime = "&6Start: &f%startTime%";
		String reason = "&6Reason: &f%reason%";
		String bannedBy = "&6Banned By: &f%bannedBy%";

		if (plugin.mysqlEnabled) {
			DatabaseIPBansManager dbManager = new DatabaseIPBansManager(plugin);
			startTime = startTime.replace("%startTime%", dbManager.getInfo(ipStr, "START"));
			reason = reason.replace("%reason%", dbManager.getInfo(ipStr, "REASON"));
			bannedBy = bannedBy.replace("%bannedBy%", dbManager.getInfo(ipStr, "BANNEDBY"));

		} else {
			ConfigIPBansManager confManager = new ConfigIPBansManager(plugin);
			startTime = startTime.replace("%startTime%", confManager.getInfo(ipStr, "Start"));
			reason = reason.replace("%reason%", confManager.getInfo(ipStr, "Reason"));
			bannedBy = bannedBy.replace("%bannedBy%", confManager.getInfo(ipStr, "Banned By"));
		}
		sender.sendMessage(Utils.chat(ip));
		sender.sendMessage(Utils.chat(startTime));
		sender.sendMessage(Utils.chat(reason));
		sender.sendMessage(Utils.chat(bannedBy));
	}

}
