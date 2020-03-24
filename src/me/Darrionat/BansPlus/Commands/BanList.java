package me.Darrionat.BansPlus.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Handlers.Bans.ConfigBansManager;
import me.Darrionat.BansPlus.Handlers.Bans.DatabaseBansManager;
import me.Darrionat.BansPlus.Handlers.IPBans.ConfigIPBansManager;
import me.Darrionat.BansPlus.Handlers.IPBans.DatabaseIPBansManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class BanList implements CommandExecutor {
	private Main plugin;

	public BanList(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("banlist").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.banlist";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length == 0) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/banlist [player/ip]"));
			return true;
		}
		if (args.length != 0 && !args[0].equalsIgnoreCase("player") && !args[0].equalsIgnoreCase("ip")) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/banlist [player/ip]"));
			return true;

		}
		String emptyListMsg = Utils.chat(config.getString("Messages.No Bans"));
		if (args[0].equalsIgnoreCase("player")) {
			if (plugin.mysqlEnabled) {
				DatabaseBansManager dbManager = new DatabaseBansManager(plugin);
				if (dbManager.getList().isEmpty()) {
					sender.sendMessage(emptyListMsg);
					return true;
				}
				sender.sendMessage(Utils.chat(putBanListInString(dbManager.getList(), false)));
				return true;
			} else {
				ConfigBansManager confManager = new ConfigBansManager(plugin);
				if (confManager.getList().isEmpty()) {
					sender.sendMessage(emptyListMsg);
					return true;
				}
				sender.sendMessage(Utils.chat(putBanListInString(confManager.getList(), false)));
				return true;
			}
		}
		if (args[0].equalsIgnoreCase("ip")) {
			if (plugin.mysqlEnabled) {
				DatabaseIPBansManager dbIPsManager = new DatabaseIPBansManager(plugin);
				if (dbIPsManager.getList().isEmpty()) {
					sender.sendMessage(emptyListMsg);
					return true;
				}
				sender.sendMessage(Utils.chat(putBanListInString(dbIPsManager.getList(), true)));
				return true;

			} else {
				ConfigIPBansManager confIPsManager = new ConfigIPBansManager(plugin);
				if (confIPsManager.getList().isEmpty()) {
					sender.sendMessage(emptyListMsg);
					return true;
				}
				sender.sendMessage(Utils.chat(putBanListInString(confIPsManager.getList(), true)));
				return true;
			}
		}

		return true;
	}

	public String putBanListInString(List<String> list, Boolean ifIP) {
		String banListString = "";
		String last = list.get(list.size() - 1);
		for (String s : list) {
			if (ifIP) {
				s = s.replace("-", ".");
			}
			if (last.equals(s)) {
				banListString = banListString + s;
				break;
			}
			banListString = banListString + s + ", ";
		}
		String banListPrefix = Utils.chat(plugin.getConfig().getString("Messages.BanListPrefix"));
		return banListPrefix + " " + Utils.chat(banListString);
	}
}