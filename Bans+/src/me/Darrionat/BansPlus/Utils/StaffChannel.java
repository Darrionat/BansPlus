package me.Darrionat.BansPlus.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.Darrionat.BansPlus.Main;

public class StaffChannel {

	private Main plugin;

	public StaffChannel(Main plugin) {
		this.plugin = plugin;
	}

	public void sendStaffMessage(String s) {
		// TODO Auto-generated constructor stub
		String prefix = Utils.chat(plugin.getConfig().getString("StaffChannel.Prefix"));
		String msg = Utils.chat(s);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.hasPermission("bansplus.staff")) {
				continue;
			}
			p.sendMessage(prefix + msg);
		}
		System.out.println(prefix + msg);
	}
	// Future Bungee-Messaging

}
