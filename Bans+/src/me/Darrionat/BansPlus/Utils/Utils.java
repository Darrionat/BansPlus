package me.Darrionat.BansPlus.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Handlers.Bans.ConfigBansManager;
import me.Darrionat.BansPlus.Handlers.Bans.DatabaseBansManager;

public class Utils {
	private Main plugin;

	public Utils(Main plugin) {
		this.plugin = plugin;
	}

	public static String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);

	}

	public static ItemStack createItem(Inventory inv, Material material, int amount, int invSlot, String displayName,
			String... loreString) {
		ItemStack item;
		List<String> lore = new ArrayList<String>();

		item = new ItemStack(material, amount);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.chat(displayName));
		for (String s : loreString) {
			lore.add(Utils.chat(s));
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(invSlot - 1, item);
		return item;

	}

	public static ItemStack createBanItem(Inventory inv, Material material, int amount, int invSlot,
			String displayName, List<String> loreString, String length) {
		ItemStack item;
		List<String> lore = new ArrayList<String>();

		item = new ItemStack(material, amount);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.chat(displayName));
		lore.add(Utils.chat("&eLength: &f" + length));
		for (String s : loreString) {
			lore.add(Utils.chat(s));
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(invSlot - 1, item);
		return item;

	}

	public static String IPbanMessage(JavaPlugin plugin, String ip) {

		return null;

	}

	public static String banMessage(JavaPlugin plugin, String bPlayerUUID) {
		FileConfiguration config = plugin.getConfig();
		List<String> banMessageList = config.getStringList("Ban Message");
		String message = "";
		for (String line : banMessageList) {
			Utils utils = new Utils((Main) plugin);
			String newLine = utils.banPlaceholders(line, bPlayerUUID);
			// First line
			if (message.equalsIgnoreCase("")) {
				message = newLine + "\n";
				continue;
			}
			message = message + newLine + "\n";
			continue;
		}

		return message;
	}

	private Date endDate;

	public String banPlaceholders(String line, String bPlayerUUID) {
		String endDateString = "";
		if (plugin.mysqlEnabled) {
			DatabaseBansManager dbManager = new DatabaseBansManager(plugin);
			line = line.replace("%bannedby%", dbManager.getInfo(bPlayerUUID, "BANNEDBY"));
			line = line.replace("%startdate%", dbManager.getInfo(bPlayerUUID, "START"));
			line = line.replace("%enddate%", dbManager.getInfo(bPlayerUUID, "END"));
			line = line.replace("%reason%", dbManager.getInfo(bPlayerUUID, "REASON"));
			line = line.replace("%uuid%", bPlayerUUID);
			if (dbManager.getInfo(bPlayerUUID, "END").equalsIgnoreCase("Permanent")) {
				line = line.replace("%timeleft%", "Permanent Ban");
				return line;
			}
			endDateString = dbManager.getInfo(bPlayerUUID, "END");
		} else {
			ConfigBansManager confManager = new ConfigBansManager(plugin);

			line = line.replace("%bannedby%", confManager.getInfo(bPlayerUUID, "Banned By"));
			line = line.replace("%startdate%", confManager.getInfo(bPlayerUUID, "Start"));
			line = line.replace("%enddate%", confManager.getInfo(bPlayerUUID, "End"));
			line = line.replace("%reason%", confManager.getInfo(bPlayerUUID, "Reason"));
			line = line.replace("%uuid%", bPlayerUUID);
			if (confManager.getInfo(bPlayerUUID, "End").equalsIgnoreCase("Permanent")) {
				line = line.replace("%timeleft%", "Permanent Ban");
				return line;
			}
			endDateString = confManager.getInfo(bPlayerUUID, "End");
		}

		// If it isn't a perm ban. Get the difference
		try {
			endDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(endDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long diff = endDate.getTime() - System.currentTimeMillis();
		diff = diff / 1000; // Milliseconds to seconds
		long hours = diff / 3600;
		long minutes = (diff % 3600) / 60;
		long seconds = diff % 60;
		String timeLeft = String.format("%02dh %02dm %02ds", hours, minutes, seconds);
		line = line.replace("%timeleft%", timeLeft);
		return line;

	}

	private long adder;

	public Date getEndDate(String arg, CommandSender sender) {
		arg = arg.toLowerCase();
		HashMap<String, Long> abbrevLength = new HashMap<String, Long>();
		abbrevLength.put("s", adder = 1);
		abbrevLength.put("m", adder = 60);
		abbrevLength.put("h", adder = 3600);
		abbrevLength.put("d", adder = 86400);
		abbrevLength.put("y", adder = (86400 * 365));

		for (String key : abbrevLength.keySet()) {
			if (!arg.contains(key)) {
				continue;
			}
			arg = arg.replace(key, "");
			adder = abbrevLength.get(key);
			if (getEndDateHelper(arg, sender) == null) {
				return null;
			}
			adder = abbrevLength.get(key);
			return getEndDateHelper(arg, sender);
		}
		return null;

	}

	public Date getEndDateHelper(String arg, CommandSender sender) {
		Date endDate = new Date();
		long length;
		try {
			length = Integer.parseInt(arg);
		} catch (NumberFormatException exe) {
			return null;
		}
		adder = adder * 1000 * length;
		endDate.setTime(System.currentTimeMillis() + adder);
		return endDate;
	}

	public void sendAbbrevMessages(CommandSender sender) {
		sender.sendMessage(Utils.chat("&6Seconds&7=&6s"));
		sender.sendMessage(Utils.chat("&6Minutes&7=&6m"));
		sender.sendMessage(Utils.chat("&6Hours&7=&6h"));
		sender.sendMessage(Utils.chat("&6Days&7=&6d"));
		sender.sendMessage(Utils.chat("&6Years&7=&6y"));
	}
}
