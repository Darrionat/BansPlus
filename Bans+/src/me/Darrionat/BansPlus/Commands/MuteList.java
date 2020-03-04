package me.Darrionat.BansPlus.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Handlers.Mutes.ConfigMutesManager;
import me.Darrionat.BansPlus.Handlers.Mutes.DatabaseMutesManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class MuteList implements CommandExecutor {
	private Main plugin;

	public MuteList(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("mutelist").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.mutelist";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		String emptyListMsg = Utils.chat(config.getString("Messages.No Bans"));

		if (plugin.mysqlEnabled) {
			DatabaseMutesManager dbManager = new DatabaseMutesManager(plugin);
			if (dbManager.getList().isEmpty()) {
				sender.sendMessage(emptyListMsg);
				return true;
			}
			sender.sendMessage(Utils.chat(putBanListInString(dbManager.getList(), false)));
			return true;
		}
		ConfigMutesManager confManager = new ConfigMutesManager(plugin);
		if (confManager.getList().isEmpty()) {
			sender.sendMessage(emptyListMsg);
			return true;
		}
		sender.sendMessage(Utils.chat(putBanListInString(confManager.getList(), false)));
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