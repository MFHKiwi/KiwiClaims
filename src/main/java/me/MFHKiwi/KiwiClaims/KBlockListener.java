package me.MFHKiwi.KiwiClaims;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class KBlockListener extends BlockListener {
	private final KiwiClaims plugin;
	private final List<KClaim> claims;
	
	public KBlockListener(KiwiClaims plugin) {
		this.plugin = plugin;
		claims = plugin.getClaims();
	}
	
	public boolean commonHandler(Location block_location, Player player) {
		String player_name = player.getName();
		for (KClaim claim : claims) {
			if (claim.contains(block_location)) {
				if (!(player_name.equals(claim.getOwnerName())) && 
					!(claim.getTrusted().contains(player_name))) {
					return true;
				}
			}
		}
		return false;
	}
		
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getPlayer().hasPermission("kc.admin")) {
			if (commonHandler(event.getBlock().getLocation(), event.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.getPlayer().hasPermission("kc.admin")) {
			if (commonHandler(event.getBlock().getLocation(), event.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}
	
	public void onBlockFlow(BlockFromToEvent event) {
		for (KClaim claim : claims) {
			if (claim.contains(event.getToBlock().getLocation()) && !claim.contains(event.getBlock().getLocation())) {
				event.setCancelled(true);
			}
		}
	}
}
