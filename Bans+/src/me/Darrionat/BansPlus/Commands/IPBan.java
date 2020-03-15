package me.Darrionat.BansPlus.Commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Handlers.IPBans.ConfigIPBansManager;
import me.Darrionat.BansPlus.Handlers.IPBans.DatabaseIPBansManager;
import me.Darrionat.BansPlus.Utils.StaffChannel;
import me.Darrionat.BansPlus.Utils.Utils;

public class IPBan implements CommandExecutor {
	private Main plugin;

	public IPBan(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("ipban").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.ipban";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length == 0 || args.length == 1) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/ipban [ip] [reason]"));
			return true;
		}
		// ban user reason == No GUI

		// bPlayer = bannedPlayer

		List<String> reasonArgs = new ArrayList<String>();
		for (int i = 1; i <= 256; i++) {
			try {
				reasonArgs.add(args[i]);
			} catch (ArrayIndexOutOfBoundsException exe) {
				break;
			}
		}
		String reason = "";
		for (String word : reasonArgs) {
			// First word
			if (reason.equalsIgnoreCase("")) {
				reason = word;
				continue;
			}
			reason = reason + " " + word;
		}

		Date startDate = new Date();
		startDate.setTime(System.currentTimeMillis());

		String bannedBy = "";
		if (sender instanceof Player) {
			Player p = (Player) sender;
			bannedBy = p.getName();
		} else {
			bannedBy = "Console";
		}

		ConfigIPBansManager configIPManager = new ConfigIPBansManager(plugin);
		DatabaseIPBansManager dbIPManager = new DatabaseIPBansManager(plugin);
		if (plugin.mysqlEnabled) {
			dbIPManager.createIP(args[0], startDate, reason, bannedBy);
		} else {
			configIPManager.createIP(args[0].replace(".", "-"), startDate, reason, bannedBy);
		}
		StaffChannel sChannel = new StaffChannel(plugin);
		sChannel.sendStaffMessage((
				Utils.chat(config.getString("Messages.IP Ban").replace("%ip%", args[0]).replace("%reason%", reason))));

		return true;
	}

}
