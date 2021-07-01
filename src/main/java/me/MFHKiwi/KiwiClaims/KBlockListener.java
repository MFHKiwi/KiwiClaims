package me.MFHKiwi.KiwiClaims;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class KBlockListener extends BlockListener {
	private final KiwiClaims plugin;
	
	public KBlockListener(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public boolean commonHandler(Location block_location, Player player) {
		String player_name = player.getName();
		KClaim claim = plugin.getClaimSave().getClaimAt(block_location);
		if (claim == null) return false;
		if (!(player_name.equals(claim.getOwnerName())) && 
			!(claim.getTrusted().contains(player_name))) {
			return true;
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
	
	public void onBlockFromTo(BlockFromToEvent event) {
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getToBlock().getLocation());
		if (claim == null) return;
		if (!claim.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
}
