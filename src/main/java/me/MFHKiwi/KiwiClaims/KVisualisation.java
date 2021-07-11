package me.MFHKiwi.KiwiClaims;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KVisualisation implements Runnable {
	private final Player player;
	private final Location minA, minB, maxA, maxB;
	private final List<Location> locations = new ArrayList<Location>();
	
	public enum Type {INFO, ERROR};
	private final Type type;
	
	public KVisualisation(Player player, KClaim claim, Type type) {
		this(player, claim.getMin(), claim.getMax(), type);
	}
	
	public KVisualisation(Player player, Location min, Location max, Type type) {
		this.player = player;
		this.minA = min;
		this.minB = new Location(min.getWorld(), min.getX(), min.getY(), max.getZ());
		this.maxA = max;
		this.maxB = new Location(min.getWorld(), max.getX(), max.getY(), min.getZ());
		this.type = type;
	}
	
	private void pillar(Player player, Location location) {
		Material material = (type == Type.INFO) ? Material.GLOWSTONE : Material.OBSIDIAN;
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
		pillar(player, this.minA);
		pillar(player, this.maxA);
		pillar(player, this.minB);
		pillar(player, this.maxB);
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {}
		restore(player, this.locations);
	}
}