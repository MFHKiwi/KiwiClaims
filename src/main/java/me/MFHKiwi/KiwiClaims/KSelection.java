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
}
