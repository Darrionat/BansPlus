package me.Darrionat.BansPlus.Commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Listeners.CPSTest;
import me.Darrionat.BansPlus.Utils.StaffChannel;
import me.Darrionat.BansPlus.Utils.Utils;

public class CPS implements CommandExecutor {
	private Main plugin;

	public CPS(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("cps").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.cps";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length != 2) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/cps [player] [duration]"));
			return true;
		}
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
		if (Bukkit.getOfflinePlayer(args[0]) == null) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Invalid Player").replace("%name%", args[0])));
			return true;
		}
		if (!offlinePlayer.isOnline()) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Player Is Offline")));
			return true;
		}
		int duration;
		try {
			duration = Integer.parseInt(args[1]);
		} catch (NumberFormatException exe) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Not A Number")));
			return true;
		}
		Player p = (Player) offlinePlayer;
		HashMap<Player, Integer> cpsTestMap = CPSTest.getCpsTestList();
		cpsTestMap.put(p, 0);
		sender.sendMessage(Utils.chat(config.getString("Messages.CPSTesting").replace("%player%", p.getName())
				.replace("%duration%", String.valueOf(duration))));
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				StaffChannel staffChat = new StaffChannel(plugin);
				staffChat.sendStaffMessage(
						Utils.chat("&6" + p.getName() + " Average CPS: &f" + cpsTestMap.get(p) / duration));
				cpsTestMap.remove(p);
			}
		}, 20L * duration);// 60 L == 3 sec, 20 ticks == 1 sec

		return true;
	}
}