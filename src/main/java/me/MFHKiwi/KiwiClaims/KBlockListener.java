package me.MFHKiwi.KiwiClaims;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class KBlockListener extends BlockListener {
	private final KiwiClaims plugin; 
	
	public KBlockListener(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		List<KClaim> claims = plugin.getClaims();
		for (KClaim claim : claims) {
			if (!player.equals(claim.owner)) {
				if (claim.contains(block)) {
					event.setCancelled(true);
				}
			}
		}
	}
}
