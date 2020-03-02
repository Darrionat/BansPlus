package me.Darrionat.BansPlus.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Utils.Utils;

public class BansPlus implements CommandExecutor {
	private Main plugin;

	public BansPlus(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("bansplus").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.bansplus";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		if (args.length == 0) {
			sendBaseMessage(sender);
			return true;
		}
		if (args.length != 0 && !args[0].equalsIgnoreCase("help")) {
			sendBaseMessage(sender);
		}
		// /bansplus help
		sendMessage(sender, "&6&lBans+ v" + plugin.getDescription().getVersion() + " Commands");
		sendMessage(sender, "&6/ban [user] &f- GUI to Ban a Player");
		sendMessage(sender, "&6/ban [user] reason &f- Ban With A Custom Reason");
		sendMessage(sender, "&6/banlist [player/ip] &f- Lists of Bans for Players or IPs");
		sendMessage(sender, "&6/ipban [ip] [reason] &f- Ban an IP");
		sendMessage(sender, "&6/tempban [user] [time] [reason] &f- Temporarily Ban a Player");
		sendMessage(sender, "&6/unban [user/ip] &f- Unban a Player or IP");
		return true;
	}

	public void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(Utils.chat(msg));
	}

	public void sendBaseMessage(CommandSender sender) {
		sendMessage(sender, "&6&lBans+ v" + plugin.getDescription().getVersion() + " By: Darrionat");
		sendMessage(sender, "&6Type '/bansplus help' for commands");
	}

}
