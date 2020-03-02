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
import me.Darrionat.BansPlus.Handlers.Bans.ConfigBansManager;
import me.Darrionat.BansPlus.Handlers.Bans.DatabaseBansManager;
import me.Darrionat.BansPlus.UI.BanUI;
import me.Darrionat.BansPlus.Utils.Utils;

public class Ban implements CommandExecutor {
	private Main plugin;

	public Ban(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("ban").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.ban";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length == 0) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/ban [player] [reason]"));
			return true;
		}
		if (Bukkit.getOfflinePlayer(args[0]) == null) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Invalid Player").replace("%name%", args[0])));
			return true;
		}
		// ban user = GUI
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(cmdMsgs.incorrectUsage("/ban [player] [reason]"));
				return true;
			}
			Player p = (Player) sender;
			p.openInventory(BanUI.GUI(p, plugin));
			return true;
		}
		// ban user reason == No GUI

		// bPlayer = bannedPlayer
		OfflinePlayer bPlayer = Bukkit.getOfflinePlayer(args[0]);
		if (bPlayer.isOp() && config.getBoolean("Ban Opped Players") == false) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Player Is Op")));
			return true;
		}
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
		if (player.getName() == null) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Invalid Player").replace("%name%", args[0])));
			return true;
		}

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

		Date endDate = null;

		String bPlayerUUID = bPlayer.getUniqueId().toString();
		String bannedBy = "";
		if (sender instanceof Player) {
			Player p = (Player) sender;
			bannedBy = p.getName();
		} else {
			bannedBy = "Console";
		}

		sender.sendMessage(Utils.chat(config.getString("Messages.Permanent Ban").replace("%name%", bPlayer.getName())
				.replace("%reason%", reason)));

		ConfigBansManager configManager = new ConfigBansManager(plugin);
		DatabaseBansManager dbManager = new DatabaseBansManager(plugin);
		if (plugin.mysqlEnabled) {
			dbManager.createPlayer(bPlayer, startDate, endDate, reason, args[0], bannedBy);
		} else {
			configManager.useConfig(bPlayer, startDate, endDate, reason, args[0], bannedBy);
		}
		if (bPlayer.isOnline()) {
			Player bPlayerOnline = (Player) bPlayer;
			bPlayerOnline.kickPlayer(Utils.chat(Utils.banMessage(plugin, bPlayerUUID)));
		}

		return true;
	}

}
