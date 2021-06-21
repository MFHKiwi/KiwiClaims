package me.MFHKiwi.KiwiClaims;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class KBlockListener extends BlockListener {
	private final KiwiClaims plugin; 
	
	public KBlockListener(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		Location block_location = event.getBlock().getLocation();
		Player player = event.getPlayer();
		String player_name = player.getName();
		List<KClaim> claims = plugin.getClaims();
		for (KClaim claim : claims) {
			if ((!player_name.equals(claim.getOwnerName())) && (!claim.getTrusted().contains(player_name))) {
				if (claim.contains(block_location)) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		Location block_location = event.getBlock().getLocation();
		Player player = event.getPlayer();
		String player_name = player.getName();
		List<KClaim> claims = plugin.getClaims();
		for (KClaim claim : claims) {
			if ((!player_name.equals(claim.getOwnerName())) && (!claim.getTrusted().contains(player_name))) {
				if (claim.contains(block_location)) {
					event.setCancelled(true);
				}
			}
		}
	}
}
