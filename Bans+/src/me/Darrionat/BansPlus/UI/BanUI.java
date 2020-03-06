package me.Darrionat.BansPlus.UI;

import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.Darrionat.BansPlus.Main;
import me.Darrionat.BansPlus.Commands.Ban;
import me.Darrionat.BansPlus.Files.FileManager;
import me.Darrionat.BansPlus.Handlers.Bans.ConfigBansManager;
import me.Darrionat.BansPlus.Handlers.Bans.DatabaseBansManager;
import me.Darrionat.BansPlus.Utils.Utils;

public class BanUI {

	private Main plugin;
	public static Inventory inv;
	public static String inventory_name;
	public static int rows;
	public OfflinePlayer bPlayer;

	public BanUI(Main plugin) {
		this.plugin = plugin;
	}

	public void initialize(JavaPlugin plugin) {
		FileConfiguration banGUI = getBanGUI();
		inventory_name = Utils.chat(banGUI.getString("GUIName"));
		rows = banGUI.getInt("Rows");
		inv = Bukkit.createInventory(null, rows * 9);

	}

	public FileConfiguration getBanGUI() {
		FileManager fileManager = new FileManager((Main) plugin);
		FileConfiguration banGUI = fileManager.getDataConfig("bangui");
		return banGUI;
	}

	public Inventory GUI(Player p) {
		FileConfiguration banGUI = getBanGUI();
		Inventory toReturn = Bukkit.createInventory(null, rows * 9, inventory_name);
		for (String key : banGUI.getKeys(false)) {
			if (key.equalsIgnoreCase("Rows") || key.equalsIgnoreCase("GUIName")) {
				continue;
			}
			int slot;
			try {
				slot = Integer.parseInt(key);
			} catch (NumberFormatException exe) {
				p.sendMessage(
						Utils.chat("&4[Bans+] &cError: Slot '" + key + "' should be a number in the bangui.yml file"));
				continue;
			}
			ConfigurationSection keySection = banGUI.getConfigurationSection(key);
			int amt = keySection.getInt("Amount");
			Material material = Material.getMaterial(keySection.getString("Material"));
			String name = keySection.getString("Name");
			List<String> lore = keySection.getStringList("Lore");
			String length = keySection.getString("Length");
			Utils.createBanItem(inv, material, amt, slot, name, lore, length);

		}
		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		slot++;
		if (clicked.getItemMeta().getDisplayName() == null) {
			return;
		}
		FileConfiguration banGUI = getBanGUI();
		if (banGUI.getConfigurationSection(String.valueOf(slot)) == null) {
			return;
		}
		ConfigurationSection banType = banGUI.getConfigurationSection(String.valueOf(slot));
		Utils utils = new Utils(plugin);

		Date startDate = new Date();
		startDate.setTime(System.currentTimeMillis());

		String length = banType.getString("Length");

		Date endDate = null;

		if (!banType.getString("Length").equalsIgnoreCase("Permanent")) {
			endDate = utils.getEndDate(length, p);
			if (endDate == null) {
				utils.sendAbbrevMessages(p);
				p.sendMessage(
						Utils.chat("&4[Bans+] &cError:You must fix the 'Length' section in your bangui.yml file"));
				p.closeInventory();
				return;
			}
		}

		String reason = ChatColor.stripColor(Utils.chat(banType.getString("Name")));

		bPlayer = Ban.bPlayer;
		String bName = Ban.bName;

		if (plugin.mysqlEnabled) {
			DatabaseBansManager dbManager = new DatabaseBansManager(plugin);

			dbManager.createPlayer(bPlayer, startDate, endDate, reason, bName, p.getName());
			p.closeInventory();
			p.sendMessage(Utils.chat(plugin.getConfig().getString("Messages.Temporary Ban")
					.replace("%name%", bPlayer.getName()).replace("%reason%", reason)).replace("%time%", length));
			return;
		}
		ConfigBansManager confManager = new ConfigBansManager(plugin);

		confManager.useConfig(bPlayer, startDate, endDate, reason, bName, p.getName());
		p.closeInventory();
		p.sendMessage(Utils.chat(plugin.getConfig().getString("Messages.Temporary Ban")
				.replace("%name%", bPlayer.getName()).replace("%reason%", reason)).replace("%time%", length));

	}

}
