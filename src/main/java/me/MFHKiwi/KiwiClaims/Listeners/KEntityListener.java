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

import java.util.Iterator;

import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.MFHKiwi.KiwiClaims.KClaim;
import me.MFHKiwi.KiwiClaims.KiwiClaims;

public class KEntityListener extends EntityListener {
	private final KiwiClaims plugin;
	
	public KEntityListener(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public void registerEvents() {
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.PAINTING_BREAK, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.PAINTING_PLACE, (Listener) this, Event.Priority.High, (Plugin) plugin);
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
		if (this.plugin.shouldPrevent(player, claim)) {
			event.setCancelled(true);
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
	
	public void onPaintingBreak(PaintingBreakEvent event) {
		if (!(event instanceof PaintingBreakByEntityEvent)) return;
		PaintingBreakByEntityEvent event2 = (PaintingBreakByEntityEvent) event;
		KClaim claim = plugin.getClaimSave().getClaimAt(event2.getPainting().getLocation());
		if (claim == null) return;
		if (!(event2.getRemover() instanceof Player)) { 
			event.setCancelled(true);
			return;
		}
		Player player = (Player) event2.getRemover();
		if (this.plugin.shouldPrevent(player, claim)) {
			event.setCancelled(true);
		}
	}
	
	public void onPaintingPlace(PaintingPlaceEvent event) {
		Player player = event.getPlayer();
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getPainting().getLocation());
		if (claim == null) return;
		if (this.plugin.shouldPrevent(player, claim)) {
			event.setCancelled(true);
		}
	}
}
