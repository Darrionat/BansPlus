package me.Darrionat.BansPlus.UI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.Darrionat.BansPlus.Utils.Utils;

public class BanUI {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5;
	public static int size = inv_rows * 9;

	public static void initialize(JavaPlugin plugin) {
		inventory_name = Utils.chat(plugin.getConfig().getString("GUI Name"));
		inv = Bukkit.createInventory(null, size);
	}

	public static Inventory GUI(Player p, JavaPlugin plugin) {
		Inventory toReturn = Bukkit.createInventory(null, size, inventory_name);

		toReturn.setContents(inv.getContents());

		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv, JavaPlugin plugin) {
		if (clicked.getItemMeta().getDisplayName() == null) {
			return;
		}

	}

}
