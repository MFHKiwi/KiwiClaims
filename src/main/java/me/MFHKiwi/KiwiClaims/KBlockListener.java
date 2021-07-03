package me.MFHKiwi.KiwiClaims;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class KBlockListener extends BlockListener {
	private final KiwiClaims plugin;
	private final String[] not_allowed = new String[3];
	
	public KBlockListener(KiwiClaims plugin) {
		this.plugin = plugin;
		ChatColor colour1 = plugin.getColour(1);
		ChatColor colour2 = plugin.getColour(2);
		this.not_allowed[0] = colour1 + "You are not allowed to build here!";
		this.not_allowed[1] = colour1 + "Ask the owner of this claim, " + colour2;
		this.not_allowed[2] = colour1 + ", for permission.";
	}
	
	public boolean commonHandler(Location block_location, Player player) {
		String player_name = player.getName();
		KClaim claim = plugin.getClaimSave().getClaimAt(block_location);
		if (claim == null) return false;
		if (!(player_name.equals(claim.getOwnerName())) && 
			!(claim.getTrusted().contains(player_name))) {
			player.sendMessage(this.not_allowed[0]);
			player.sendMessage(this.not_allowed[1] + claim.getOwnerName() + this.not_allowed[2]);
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
