package me.Darrionat.BansPlus.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.Darrionat.BansPlus.Main;

public class Punishments implements CommandExecutor {
	private Main plugin;
	public Punishments(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("punishments").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		@SuppressWarnings("unused")
		FileConfiguration config = plugin.getConfig();

		return true;
	}

	

}
