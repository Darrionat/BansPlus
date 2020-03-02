package me.Darrionat.BansPlus.Commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

public class Mute implements CommandExecutor {
	private Main plugin;

	public Mute(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("mute").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.mute";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length == 0) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/mute [player] [length] [reason]"));
			return true;
		}
		if (Bukkit.getOfflinePlayer(args[0]) == null) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Invalid Player").replace("%name%", args[0])));
			return true;
		}
		// ban user = GUI
		if (args.length == 1) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/mute [player] [length] [reason]"));
			return true;
		}
		// tempban user length reason

		// bPlayer = bannedPlayer
		OfflinePlayer mPlayer = Bukkit.getOfflinePlayer(args[0]);
		if (mPlayer.isOp() && config.getBoolean("Ban Opped Players") == false) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Player Is Op")));
			return true;
		}

		List<String> reasonArgs = new ArrayList<String>();
		for (int i = 2; i <= 256; i++) {
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
		Utils utils = new Utils(plugin);
		Date endDate = utils.getEndDate(args[1], sender);
		if (endDate == null) {
			utils.sendAbbrevMessages(sender);
			return true;
		}

		String mutedBy = "";
		if (sender instanceof Player) {
			Player p = (Player) sender;
			mutedBy = p.getName();
		} else {
			mutedBy = "Console";
		}

		sender.sendMessage(Utils.chat(config.getString("Messages.Muted Successfully")
				.replace("%name%", mPlayer.getName()).replace("%reason%", reason)).replace("%time%", args[1]));

		ConfigMutesManager configMutesManager = new ConfigMutesManager(plugin);
		DatabaseMutesManager dbMutesManager = new DatabaseMutesManager(plugin);
		if (plugin.mysqlEnabled) {
			dbMutesManager.createPlayer(mPlayer, startDate, endDate, reason, args[0], mutedBy);
		} else {
			configMutesManager.useConfig(mPlayer, startDate, endDate, reason, args[0], mutedBy);
		}

		if (mPlayer.isOnline()) {
			Player mPlayerOnline = (Player) mPlayer;
			mPlayerOnline
					.sendMessage(Utils.chat(config.getString("Messages.To Muted Player").replace("%reason%", reason))
							.replace("%time%", args[1]));
		}

		return true;
	}

}
