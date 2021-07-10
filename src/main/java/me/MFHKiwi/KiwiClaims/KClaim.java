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
import java.util.UUID;

import org.bukkit.Location;

public class KClaim {
	private Location min, max;
	private String owner_name;
	private List<String> trusted_names = new ArrayList<String>();
	private UUID uuid;
	
	public KClaim(Location min, Location max, String owner_name, UUID uuid) {
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
		this.owner_name = owner_name.toLowerCase();
		this.uuid = uuid;
	}
	
	public KClaim(Location min, Location max, String owner_name, UUID uuid, List<String> trusted_names) {
		this(min, max, owner_name, uuid);
		this.trusted_names = trusted_names;
	}
	
	public KClaim(KSelection selection) {
		this(selection.getMin(), selection.getMax(), selection.getPlayerName(), UUID.randomUUID());
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
	
	public Location getMin() {
		return this.min;
	}
	
	public Location getMax() {
		return this.max;
	}
	
	public void setOwnerName(String name) {
		this.owner_name = name.toLowerCase();
	}
	
	public String getOwnerName() {
		return this.owner_name;
	}
	
	public boolean ownerEquals(String name) {
		if (name.equalsIgnoreCase(this.owner_name)) return true;
		return false;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public boolean isTrusted(String name) {
		for (String trusted_name : this.trusted_names) {
			if (name.equalsIgnoreCase(trusted_name)) return true;
		}
		return false;
	}
	
	public List<String> getTrusted() {
		return this.trusted_names;
	}
	
	public boolean addTrusted(String name) {
		if (!this.trusted_names.contains(name.toLowerCase()) ) {
			this.trusted_names.add(name.toLowerCase());
			return true;
		} else {
			return false;
		}
	}
	
	public boolean removeTrusted(String name) {
		String lowercase_name = name.toLowerCase();
		if (this.trusted_names.contains(lowercase_name)) {
			this.trusted_names.remove(lowercase_name);
			return true;
		} else {
			return false;
		}
	}
}
