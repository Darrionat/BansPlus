package me.Darrionat.BansPlus.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Handlers.Mutes.ConfigMutesManager;
import me.Darrionat.BansPlus.Handlers.Mutes.DatabaseMutesManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class Unmute implements CommandExecutor {
	private Main plugin;

	public Unmute(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("unmute").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.unmute";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}

		ConfigMutesManager confMutesManager = new ConfigMutesManager(plugin);
		DatabaseMutesManager dbMutesManager = new DatabaseMutesManager(plugin);

		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length == 0) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/unban [UUID/Player/IP]"));
			return true;
		}
		String unmuteMsg = Utils.chat(config.getString("Messages.Unmute Successful").replace("%player%", args[0]));

		String uuid = args[0];
		if (plugin.mysqlEnabled) {
			if (dbMutesManager.playerExists(uuid)) {
				dbMutesManager.removePlayer(uuid);
				sender.sendMessage(unmuteMsg);
				return true;
			}
		} else {
			if (confMutesManager.playerExists(uuid)) {
				confMutesManager.removePlayer(uuid);
				sender.sendMessage(unmuteMsg);
				return true;
			}
		}

		// Player isn't muted
		sender.sendMessage(Utils.chat(config.getString("Messages.Unmute DNE")));
		return true;
	}

}
