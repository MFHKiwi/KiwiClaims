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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Bed;

public class KBlockListener extends BlockListener {
	private final KiwiClaims plugin;
	private final String[] not_allowed = new String[3];
	
	public KBlockListener(KiwiClaims plugin) {
		this.plugin = plugin;
		ChatColor colour1 = plugin.getColour(1);
		ChatColor colour2 = plugin.getColour(2);
		this.not_allowed[0] = colour1 + "You are not allowed to build here!";
		this.not_allowed[1] = colour1 + "Ask the owner of this claim, " + colour2;
		this.not_allowed[2] = colour1 + ", for permission.";
	}
	
	public boolean commonHandler(Location block_location, Player player) {
		KClaim claim = plugin.getClaimSave().getClaimAt(block_location);
		if (claim == null) return false;
		if (!KiwiClaims.shouldPrevent(player, claim)) return false;
		player.sendMessage(this.not_allowed[0]);
		player.sendMessage(this.not_allowed[1] + claim.getOwnerName() + this.not_allowed[2]);
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
}
