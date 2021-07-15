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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KVisualisation implements Runnable {
	private final Player player;
	private final Location[] corners = new Location[4];
	private final List<Location> locations = new ArrayList<Location>();
	
	public enum Type {INFO, ERROR};
	private final Type type;
	
	public KVisualisation(Player player, KClaim claim, Type type) {
		this(player, claim.getMin(), claim.getMax(), type);
	}
	
	public KVisualisation(Player player, Location min, Location max, Type type) {
		this.player = player;
		this.corners[0] = min;
		this.corners[1] = new Location(min.getWorld(), min.getX(), min.getY(), max.getZ());
		this.corners[2] = max;
		this.corners[3] = new Location(min.getWorld(), max.getX(), max.getY(), min.getZ());
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
		for (Location corner : this.corners) {
			pillar(player, corner);
		}
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {}
		restore(player, this.locations);
	}
}