package me.MFHKiwi.KiwiClaims;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class KClaim {
	private final Location min, max;
	private final String owner_name;
	private final List<String> trusted_names = new ArrayList<String>();
	
	public KClaim(Location min, Location max, String owner_name) {
		int x1, x2, z1, z2, temp;
		x1 = min.getBlockX();
		x2 = max.getBlockX();
		z1 = min.getBlockZ();
		z2 = max.getBlockZ();
		if (x1 > x2) {
			temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (z1 > z2) {
			temp = z1;
			z1 = z2;
			z2 = temp;
		}
		this.min = new Location(min.getWorld(), x1, min.getBlockY(), z1);
		this.max = new Location(max.getWorld(), x2, max.getBlockY(), z2);
		this.owner_name = owner_name;
	}
	
	public KClaim(KSelection selection) {
		this(selection.getMin(), selection.getMax(), selection.getPlayerName());
	}
	
	public boolean contains(Location location) {
		if (!location.getWorld().equals(this.min.getWorld())) {
			return false;
		} else {
			int x = location.getBlockX();
			int z = location.getBlockZ();
			if (x >= this.min.getBlockX() &&
				x <= this.max.getBlockX() &&
				z >= this.min.getBlockZ() &&
				z <= this.max.getBlockZ()) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public Location getMinLocation() {
		return this.min;
	}
	
	public Location getMaxLocation() {
		return this.max;
	}
	
	public String getOwnerName() {
		return this.owner_name;
	}
	
	public List<String> getTrusted() {
		return this.trusted_names;
	}
	
	public boolean addTrusted(String name) {
		if (!this.trusted_names.contains(name) ) {
			this.trusted_names.add(name);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean removeTrusted(String name) {
		if (this.trusted_names.contains(name)) {
			this.trusted_names.remove(name);
			return true;
		} else {
			return false;
		}
	}
}
