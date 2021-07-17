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
package me.MFHKiwi.KiwiClaims.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.MFHKiwi.KiwiClaims.KClaim;
import me.MFHKiwi.KiwiClaims.KiwiClaims;

public class KVehicleListener extends VehicleListener {
	private final KiwiClaims plugin;
	private final String[] not_allowed = new String[3];
	
	public KVehicleListener(KiwiClaims plugin) {
		this.plugin = plugin;
		ChatColor colour1 = plugin.getColour(1);
		ChatColor colour2 = plugin.getColour(2);
		this.not_allowed[0] = colour1 + "You are not allowed to break that here!";
		this.not_allowed[1] = colour1 + "Ask the owner of this claim, " + colour2;
		this.not_allowed[2] = colour1 + ", for permission.";
	}
	
	public void registerEvents() {
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.VEHICLE_DAMAGE, (Listener) this, Event.Priority.High, (Plugin) plugin);
	}
	
	public void onVehicleDamage(VehicleDamageEvent event) {
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getVehicle().getLocation());
		if (claim == null) return;
		if (!(event.getAttacker() instanceof Player)) {
			event.setCancelled(true);
			return;
		}
		Player player = (Player) event.getAttacker();
		if (KiwiClaims.shouldPrevent(player, claim)) {
			event.setCancelled(true);
			player.sendMessage(this.not_allowed[0]);
			player.sendMessage(this.not_allowed[1] + claim.getOwnerName() + this.not_allowed[2]);
		}
	}
}
