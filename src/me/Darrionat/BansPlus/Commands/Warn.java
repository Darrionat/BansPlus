package me.Darrionat.BansPlus.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Utils.StaffChannel;
import me.Darrionat.BansPlus.Utils.Utils;

public class Warn implements CommandExecutor {
	private Main plugin;

	public Warn(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("warn").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.warn";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length == 0) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/warn [player] [reason]"));
			return true;
		}
		if (Bukkit.getPlayer(args[0]) == null) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Invalid Player").replace("%name%", args[0])));
			return true;
		}
		// ban user = GUI
		if (args.length == 1) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/warn [player] [reason]"));
			return true;
		}
		Player wPlayer = Bukkit.getPlayer(args[0]);
		if (wPlayer.isOp() && config.getBoolean("Ban Opped Players") == false) {
			sender.sendMessage(Utils.chat(config.getString("Messages.Player Is Op")));
			return true;
		}
		if (wPlayer instanceof Player) {
			Player p = (Player) wPlayer;
			if (p.hasPermission("bansplus.unbannable")) {
				sender.sendMessage(Utils.chat(config.getString("Messages.Player Is Op")));
				return true;
			}
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
		wPlayer.sendMessage(Utils.chat(config.getString("Messages.Warned").replace("%reason%", reason)));

		StaffChannel sChannel = new StaffChannel(plugin);

		sChannel.sendStaffMessage(Utils.chat(config.getString("Messages.Warned Player")
				.replace("%player%", wPlayer.getName()).replace("%reason%", reason)));

		return true;
	}

}
