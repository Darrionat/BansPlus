package me.Darrionat.BansPlus.Listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.Darrionat.BansPlus.Main;

public class CPSTest implements Listener {

	@SuppressWarnings("unused")
	private Main plugin;

	public CPSTest(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	public static HashMap<Player, Integer> cpsTestList = new HashMap<Player, Integer>();

	public static HashMap<Player, Integer> getCpsTestList() {
		return cpsTestList;
	}

	@EventHandler
	public void playerClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (!cpsTestList.containsKey(p)) {
			return;
		}
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		int clickAmt = cpsTestList.get(p);
		cpsTestList.put(p, clickAmt + 1);

	}
}