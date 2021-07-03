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

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class KEntityListener extends EntityListener {
	private final KiwiClaims plugin;
	private final String[] not_allowed = new String[3];
	
	public KEntityListener(KiwiClaims plugin) {
		this.plugin = plugin;
		ChatColor colour1 = plugin.getColour(1);
		ChatColor colour2 = plugin.getColour(2);
		this.not_allowed[0] = colour1 + "You are not allowed to hurt that here!";
		this.not_allowed[1] = colour1 + "Ask the owner of this claim, " + colour2;
		this.not_allowed[2] = colour1 + ", for permission.";
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) return;
		if (!(event.getEntity() instanceof Animals) &&
			!(event.getEntity() instanceof WaterMob)) return;
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getEntity().getLocation());
		EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
		if (claim == null) return;
		Entity damager = event2.getDamager();
		if (!(damager instanceof Player)) {
			event.setCancelled(true);
			return;
		}
		Player player = (Player) event2.getDamager();
		String player_name = player.getName();
		if (!player_name.equals(claim.getOwnerName())) {
			for (String trusted_name : claim.getTrusted()) {
				if (player_name.equals(trusted_name)) return;
			}
			event.setCancelled(true);
			player.sendMessage(this.not_allowed[0]);
			player.sendMessage(this.not_allowed[1] + claim.getOwnerName() + this.not_allowed[2]);
		}
	}
	
	public void onEntityExplode(EntityExplodeEvent event) {
		for (Iterator<Block> it = event.blockList().iterator(); it.hasNext();) {
			Block block = it.next();
			KClaim claim = plugin.getClaimSave().getClaimAt(block.getLocation());
			if (claim != null) {
				it.remove();
			}
		}
	}
}
