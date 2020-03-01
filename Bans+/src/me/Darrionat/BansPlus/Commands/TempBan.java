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
import me.Darrionat.BansPlus.Handlers.ConfigBansManager;
import me.Darrionat.BansPlus.Handlers.DatabaseBansManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class TempBan implements CommandExecutor {
	private Main plugin;

	public TempBan(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("tempban").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (args.length == 0) {
			// Send messages about plugin
			return true;
		}
		if (Bukkit.getOfflinePlayer(args[0]) == null) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Invalid Player").replace("%name%", args[0])));
			return true;
		}
		// ban user = GUI
		if (args.length == 1) {
			CommandMessages cmdMsgs = new CommandMessages(plugin);
			sender.sendMessage(cmdMsgs.incorrectUsage("/tempban [player] [length] [reason]"));
			return true;
		}
		// tempban user length reason

		// bPlayer = bannedPlayer
		OfflinePlayer bPlayer = Bukkit.getOfflinePlayer(args[0]);
		if (bPlayer.isOp() && config.getBoolean("Ban Opped Players") == false) {
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
			return true;
		}

		String bPlayerUUID = bPlayer.getUniqueId().toString();
		String bannedBy = "";
		if (sender instanceof Player) {
			Player p = (Player) sender;
			bannedBy = p.getName();
		} else {
			bannedBy = "Console";
		}

		sender.sendMessage(Utils.chat(config.getString("Messages.Temporary Ban").replace("%name%", bPlayer.getName())
				.replace("%reason%", reason)).replace("%time%", args[1]));

		ConfigBansManager configManager = new ConfigBansManager(plugin);
		DatabaseBansManager dbManager = new DatabaseBansManager(plugin);
		if (plugin.mysqlEnabled) {
			dbManager.createPlayer(bPlayer, startDate, endDate, reason, bannedBy);
		} else {
			configManager.useConfig(bPlayer, startDate, endDate, reason, bannedBy);
		}
		if (bPlayer.isOnline()) {
			Player bPlayerOnline = (Player) bPlayer;
			bPlayerOnline.kickPlayer(Utils.chat(Utils.banMessage(plugin, bPlayerUUID)));
		}

		return true;
	}

}
