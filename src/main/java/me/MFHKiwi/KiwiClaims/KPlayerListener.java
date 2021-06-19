package me.MFHKiwi.KiwiClaims;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class KPlayerListener extends PlayerListener {
	private final KiwiClaims plugin;
	
	public KPlayerListener(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			plugin.log.info(event.getPlayer().getName() + " punched a " + event.getClickedBlock().getType().toString());
		} else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			
		}
	}
}
