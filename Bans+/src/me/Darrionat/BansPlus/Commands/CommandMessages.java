package me.Darrionat.BansPlus.Commands;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Utils.Utils;

public class CommandMessages {

	@SuppressWarnings("unused")
	private Main plugin;

	public CommandMessages(Main plugin) {
		this.plugin = plugin;
	}

	public String incorrectUsage(String message) {
		return Utils.chat(plugin.getConfig().getString("Messages.Incorrect Usage Prefix") + " " + message);
	}
}
