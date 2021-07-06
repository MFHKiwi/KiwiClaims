/*
    KiwiClaims - A claims plugin for CraftBukkit 1060.
    Copyright (C) 2021  MFHKiwi

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
		if (this.min.getBlockX() <= max.getBlockX() && this.max.getBlockX() >= min.getBlockX() &&
			this.min.getBlockZ() <= max.getBlockZ() && this.max.getBlockZ() >= min.getBlockZ()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean overlaps(KClaim claim) {
		return overlaps(claim.getMin(), claim.getMax());
	}
}
