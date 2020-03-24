package me.Darrionat.BansPlus.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Utils.StaffChannel;
import me.Darrionat.BansPlus.Utils.Utils;

public class StaffChat implements CommandExecutor {
	private Main plugin;

	public StaffChat(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("staffchat").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		StaffChannel staffChannel = new StaffChannel(plugin);
		if (args.length == 0) {
			sender.sendMessage(Utils.chat(cmdMsgs.incorrectUsage("/" + label + " [message]")));
			return true;
		}
		if (!(sender instanceof Player)) {
			if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
				sender.sendMessage(Utils.chat(cmdMsgs.incorrectUsage("/" + label + " [message]")));
				return true;
			}
			String message = "";
			for (int i = 0; i <= 256; i++) {
				try {
					message = message + args[i] + " ";
				} catch (ArrayIndexOutOfBoundsException exe) {
					break;
				}
			}
			staffChannel.sendStaffMessage(Utils.chat("[Console] " + message));
			return true;
		}
		Player p = (Player) sender;
		String perm = "bansplus.staffchat";
		if (!p.hasPermission(perm)) {
			p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
			return true;
		}
		String message = "";
		for (int i = 0; i <= 256; i++) {
			try {
				message = message + args[i] + " ";
			} catch (ArrayIndexOutOfBoundsException exe) {
				break;
			}
		}
		staffChannel.sendStaffMessage(Utils.chat("[" + p.getName() + "] " + message));
		return true;
	}
}
