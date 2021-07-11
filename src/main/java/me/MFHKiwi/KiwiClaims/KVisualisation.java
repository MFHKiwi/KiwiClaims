package me.MFHKiwi.KiwiClaims;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KVisualisation implements Runnable {
	private final Player player;
	private final KClaim claim;
	private final List<Location> locations = new ArrayList<Location>();
	
	public enum Type {INFO, ERROR};
	private final Type type;
	
	public KVisualisation(Player player, KClaim claim, Type type) {
		this.player = player;
		this.claim = claim;
		this.type = type;
	}
	
	private void pillar(Player player, Location location) {
		Material material = (type == Type.INFO) ? Material.GLOWSTONE : Material.REDSTONE;
		for (int i = 0; i <= 256; i++) {
			Location location_height = new Location(location.getWorld(), location.getX(), i, location.getZ());
			player.sendBlockChange(location_height, material, (byte) 0);
			this.locations.add(location_height);
		}
	}
	
	private void restore(Player player, List<Location> locations) {
		for (Location location : locations) {
			player.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
		}
	}

	public void run() {
		pillar(player, claim.getMin());
		pillar(player, claim.getMax());
		Location min2 = new Location(claim.getMin().getWorld(), claim.getMin().getX(), claim.getMin().getY(), claim.getMax().getZ());
		Location max2 = new Location(claim.getMin().getWorld(), claim.getMax().getX(), claim.getMax().getY(), claim.getMin().getZ());
		pillar(player, min2);
		pillar(player, max2);
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {}
		restore(player, this.locations);
	}
}