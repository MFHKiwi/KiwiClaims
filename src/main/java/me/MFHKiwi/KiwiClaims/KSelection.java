package me.MFHKiwi.KiwiClaims;

import org.bukkit.Location;

public class KSelection {
	private final String player;
	private Location min, max = null;
	
	public KSelection(String player) {
		this.player = player;
	}
	
	public String getPlayerName() {
		return this.player;
	}
	
	public Location getMin() {
		return this.min;
	}
	
	public Location getMax() {
		return this.max;
	}
	
	public void setMin(Location min) {
		this.min = min;
	}
	
	public void setMax(Location max) {
		this.max = max;
	}
	
	public boolean overlaps(Location min, Location max) {
		if (this.min.getBlockX() < max.getBlockX() && this.max.getBlockX() > min.getBlockX() &&
			this.min.getBlockZ() < max.getBlockZ() && this.max.getBlockZ() > min.getBlockZ()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean overlaps(KClaim claim) {
		return overlaps(claim.getMin(), claim.getMax());
	}
}
