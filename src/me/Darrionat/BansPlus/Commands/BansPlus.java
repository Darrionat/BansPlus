package me.Darrionat.BansPlus.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Files.FileManager;
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

		if (args[0].equalsIgnoreCase("help")) {
			// /bansplus help
			if (args.length == 1) {
				helpMessagePage(sender, "1");
				return true;
			}
			// /bansplus help #
			helpMessagePage(sender, args[1]);

			return true;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				String perm = "bansplus.reload";
				if (!p.hasPermission(perm)) {
					p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
					return true;
				}
			}
			String[] fileList = plugin.getDataFolder().list();
			for (String s : fileList) {
				String fileName = s.replace(".yml", "");
				FileManager fileManager = new FileManager(plugin);
				fileManager.reloadDataFile(fileName);
			}
			sendMessage(sender, "&cAll .yml files reloaded");
			return true;
		}
		sendBaseMessage(sender);
		return true;
	}

	public List<String> getCmdMsgs() {
		List<String> cmds = new ArrayList<String>();
		cmds.add("&6/bansplus help [page] &f- Shows this message");
		cmds.add("&6/bansplus reload &f- Reloads all .yml files");
		cmds.add("&6/ban [user] &f- GUI to Ban a Player");
		cmds.add("&6/ban [user] reason &f- Ban With A Custom Reason");
		cmds.add("&6/banlist [player/ip] &f- Lists of Bans for Players or IPs");
		cmds.add("&6/mutelist &f- List of Muted Players");
		cmds.add("&6/ipban [ip] [reason] &f- Ban an IP");
		cmds.add("&6/mute [user] [time] [reason] &f- Mute a Player");
		cmds.add("&6/tempban [user] [time] [reason] &f- Temporarily Ban a Player");
		cmds.add("&6/unmute [user] &f- Unmute a Player");
		cmds.add("&6/unban [user/ip] &f- Unban a Player or IP");
		cmds.add("&6/baninfo &f- Ban information on a player");
		cmds.add("&6/ipbaninfo &f- Ban information on an IP");
		cmds.add("&6/muteinfo &f- Mute information on a player");
		cmds.add("&6/warn [player] [reason] &f- Warn on a player");
		cmds.add("&6/cps [player] [duration] &f-Test a player's average clicks per second");
		cmds.add("&6/sc toggle &f-Toggle staff chat");
		cmds.add("&6/sc [message] &f-Send a message to staff chat");
		return cmds;
	}

	private int listSize;
	private int pageAmt;

	public void helpMessagePage(CommandSender sender, String pageStr) {
		int page = 1;
		try {
			page = Integer.parseInt(pageStr);
		} catch (NumberFormatException exe) {
			sendHelpMessage(sender, getCmdMsgs(), 1);
			return;
		}
		listSize = getCmdMsgs().size();
		pageAmt = (listSize + 5 - 1) / 5;
		if (page > pageAmt) {
			helpMessagePage(sender, "1");
			return;
		}
		sendHelpMessage(sender, getCmdMsgs(), page);
		return;
	}

	public void sendHelpMessage(CommandSender sender, List<String> cmds, int page) {
		String topMsg = "&6&lBans+ v" + plugin.getDescription().getVersion() + " Commands";
		sender.sendMessage(Utils.chat(topMsg));
		for (int i = page * 5 - 5; i <= (page * 5 - 1); i++) {
			if (i == (listSize)) {
				break;
			}
			sender.sendMessage(Utils.chat(cmds.get(i)));
		}
		sender.sendMessage(Utils.chat("&6Page " + String.valueOf(page) + "/" + pageAmt));
	}

	public void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(Utils.chat(msg));
	}

	public void sendBaseMessage(CommandSender sender) {
		sendMessage(sender, "&6&lBans+ v" + plugin.getDescription().getVersion() + " By: Darrionat");
		sendMessage(sender, "&6Type '/bansplus help' for commands");
	}

}
