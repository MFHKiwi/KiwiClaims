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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.material.Bed;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.MFHKiwi.KiwiClaims.KClaim;
import me.MFHKiwi.KiwiClaims.KiwiClaims;

public class KBlockListener extends BlockListener {
	private final KiwiClaims plugin;
	
	public KBlockListener(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public void registerEvents() {
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.BLOCK_PLACE, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.BLOCK_FROMTO, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.BLOCK_SPREAD, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, (Listener) this, Event.Priority.High, (Plugin) plugin);
	}
	
	public boolean commonHandler(Location block_location, Player player) {
		KClaim claim = plugin.getClaimSave().getClaimAt(block_location);
		if (claim == null) return false;
		if (!this.plugin.shouldPrevent(player, claim)) return false;
		return true;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		if (commonHandler(event.getBlock().getLocation(), event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Location block_location = block.getLocation();
		Location head_location = block_location;
		if (block.getType().equals(Material.BED_BLOCK)) {
			Bed bed = (Bed) block.getState().getData();
			head_location = block.getRelative(bed.getFacing()).getLocation();
		}
		if (commonHandler(block_location, event.getPlayer()) ||
			commonHandler(head_location, event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	public void onBlockFromTo(BlockFromToEvent event) {
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getToBlock().getLocation());
		if (claim == null) return;
		if (!claim.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	public void onBlockSpread(BlockSpreadEvent event) {
		if (!event.getSource().getType().equals(Material.FIRE)) return;
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getBlock().getLocation());
		if (claim == null) return;
		if (!claim.contains(event.getSource().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		Location block_location = event.getBlock().getLocation();
		Location head_location = event.getBlock().getRelative(event.getDirection()).getLocation();
		KClaim claim = plugin.getClaimSave().getClaimAt(block_location);
		KClaim claim2 = plugin.getClaimSave().getClaimAt(head_location);
		if (claim == null && claim2 != null) {
			event.setCancelled(true);
			return;
		}
		for (Block block_from_list : event.getBlocks()) {
			if ((plugin.getClaimSave().getClaimAt(block_from_list.getLocation()) != null ||
				plugin.getClaimSave().getClaimAt(block_from_list.getRelative(event.getDirection()).getLocation()) != null) &&
				claim == null) {
				event.setCancelled(true);
				return;
			}
		}
	}
}
