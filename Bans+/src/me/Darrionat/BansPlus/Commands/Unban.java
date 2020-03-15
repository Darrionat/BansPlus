package me.Darrionat.BansPlus.Commands;

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
import me.Darrionat.BansPlus.Handlers.IPBans.ConfigIPBansManager;
import me.Darrionat.BansPlus.Handlers.IPBans.DatabaseIPBansManager;
import me.Darrionat.BansPlus.Utils.StaffChannel;
import me.Darrionat.BansPlus.Utils.Utils;

public class Unban implements CommandExecutor {
	private Main plugin;

	public Unban(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("unban").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String perm = "bansplus.unban";
			if (!p.hasPermission(perm)) {
				p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", perm)));
				return true;
			}
		}
		ConfigBansManager confManager = new ConfigBansManager(plugin);
		DatabaseBansManager dbManager = new DatabaseBansManager(plugin);
		ConfigIPBansManager confIPManager = new ConfigIPBansManager(plugin);
		DatabaseIPBansManager dbIPManager = new DatabaseIPBansManager(plugin);
		CommandMessages cmdMsgs = new CommandMessages(plugin);
		if (args.length == 0) {
			sender.sendMessage(cmdMsgs.incorrectUsage("/unban [UUID/Player/IP]"));
			return true;
		}
		String unbanMsg = Utils.chat(config.getString("Messages.Unban Successful").replace("%player%", args[0]));
		String ip = args[0];
		@SuppressWarnings("deprecation")
		OfflinePlayer bPlayer = Bukkit.getOfflinePlayer(args[0]);
		String buuid = bPlayer.getUniqueId().toString();

		// Player UUID
		StaffChannel sChannel = new StaffChannel(plugin);
		String uuid = args[0];
		if (plugin.mysqlEnabled) {
			if (dbIPManager.ipExists(ip)) {
				dbIPManager.removeIP(ip);
				sChannel.sendStaffMessage(unbanMsg);
				return true;
			}
			if (dbManager.playerExists(buuid)) {
				dbManager.removePlayer(buuid);
				sChannel.sendStaffMessage(unbanMsg);
				return true;
			}
			if (dbManager.playerExists(uuid)) {
				dbManager.removePlayer(uuid);
				sChannel.sendStaffMessage(unbanMsg);
				return true;
			}
		} else {
			if (confManager.playerExists(buuid)) {
				confManager.removePlayer(buuid);
				sChannel.sendStaffMessage(unbanMsg);
				return true;
			}
			if (confIPManager.ipExists(ip)) {
				confIPManager.removeIP(ip);
				sChannel.sendStaffMessage(unbanMsg);
				return true;
			}
			if (confManager.playerExists(uuid)) {
				confManager.removePlayer(uuid);
				sChannel.sendStaffMessage(unbanMsg);
				return true;
			}
		}

		// Not an IP or player that's banned
		sender.sendMessage(Utils.chat(config.getString("Messages.Unban DNE")));
		return true;
	}

}
