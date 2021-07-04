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
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class KPlayerListener extends PlayerListener {
	private final KiwiClaims plugin;
	private final List<KSelection> selections = new ArrayList<KSelection>();
	private final String internal_error, world_mismatch, overlap, claim_create;
	private final String[] pos_set = new String[4];
	private final String[] not_allowed = new String[3];
	
	public KPlayerListener(KiwiClaims plugin) {
		this.plugin = plugin;
		ChatColor colour1 = plugin.getColour(1);
		ChatColor colour2 = plugin.getColour(2);
		this.internal_error = colour1 + "Your claim could not be created due to an internal plugin error.";
		this.world_mismatch = colour1 + "Selections must be in the same world!";
		this.overlap = colour1 + "Your selection overlaps another claim!";
		this.claim_create = colour2 + "Claim created! Use " + colour1 + "/kc trust" + colour2 + " to allow others to build in it.";
		this.pos_set[0] = colour2 + "Position ";
		this.pos_set[1] = colour2 + " set (" + colour1 + "x: ";
		this.pos_set[2] = colour1 + ", z: ";
		this.pos_set[3] = colour2 + ")";
		this.not_allowed[0] = colour1 + "You are not allowed to use that here!";
		this.not_allowed[1] = colour1 + "Ask the owner of this claim, " + colour2;
		this.not_allowed[2] = colour1 + ", for permission.";
	}
	
	public boolean commonHandler(Player player, KClaim claim) {
		String player_name = player.getName();
		if (!player_name.equals(claim.getOwnerName()) &&
				!claim.getTrusted().contains(player_name) &&
				!player.hasPermission("kc.admin")) return true;
		else return false;
	}
	
	public int handleSelection(KSelection sel) {
		KSelection selection = sel;
		if (!selection.getMin().getWorld().equals(selection.getMax().getWorld())) return 2;
		int x1, z1, x2, z2, temp;
		x1 = selection.getMin().getBlockX();
		z1 = selection.getMin().getBlockZ();
		x2 = selection.getMax().getBlockX();
		z2 = selection.getMax().getBlockZ();
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
		selection.setMin(new Location(selection.getMin().getWorld(), x1, selection.getMin().getBlockY(), z1));
		selection.setMax(new Location(selection.getMax().getWorld(), x2, selection.getMax().getBlockY(), z2));
		for (KClaim claim : plugin.getClaimSave().getClaimsList()) {
			if (selection.overlaps(claim)) return 1;
		}
		try {
			plugin.getClaimSave().addClaim((new KClaim(selection)));
			return 0;
		} catch (Exception e) {
			plugin.log(e.getMessage());
			return 3;
		}
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getClickedBlock().getState() instanceof ContainerBlock && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			KClaim claim = plugin.getClaimSave().getClaimAt(event.getClickedBlock().getLocation());
			if (claim == null) return;
			if (!player.getName().equals(claim.getOwnerName()) && !claim.isTrusted(player.getName())) {
				event.setCancelled(true);
			}
		}
		if (selections.isEmpty()) return;
		for (Iterator<KSelection> it = selections.iterator(); it.hasNext();) {
			KSelection selection = it.next();
			if (!selection.getPlayerName().equals(player.getName())) continue;
			else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				selections.get(selections.indexOf(selection)).setMin(event.getClickedBlock().getLocation());
				player.sendMessage(this.pos_set[0] + "1" + this.pos_set[1] + selection.getMin().getBlockX() + this.pos_set[2] + selection.getMin().getBlockZ() + this.pos_set[3]);
			}
			else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				selections.get(selections.indexOf(selection)).setMax(event.getClickedBlock().getLocation());
				player.sendMessage(this.pos_set[0] + "2" + this.pos_set[1] + selection.getMax().getBlockX() + this.pos_set[2] + selection.getMax().getBlockZ() + this.pos_set[3]);
			}
			if (selection.getMin() != null && selection.getMax() != null) {
				switch (handleSelection(selection)) {
					case 3:
						player.sendMessage(this.internal_error);
						break;
					case 2:
						player.sendMessage(this.world_mismatch);
						break;
					case 1:
						player.sendMessage(this.overlap);
						break;
					case 0:
						player.sendMessage(this.claim_create);
						break;
				}
				it.remove();
			}
		}
	}
	
	public List<KSelection> getSelectionList() {
		return this.selections;
	}
	
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		Block bed = event.getBed();
		Player player = event.getPlayer();
		KClaim claim = plugin.getClaimSave().getClaimAt(bed.getLocation());
		if (claim == null) return;
		if (commonHandler(player, claim)) {
			event.setCancelled(true);
			player.sendMessage(this.not_allowed[0]);
			player.sendMessage(this.not_allowed[1] + claim.getOwnerName() + this.not_allowed[2]);
		}
	}
	
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getBlockClicked().getLocation());
		if (claim == null) return;
		if (commonHandler(player, claim)) {
			event.setCancelled(true);
			player.sendMessage(this.not_allowed[0]);
			player.sendMessage(this.not_allowed[1] + claim.getOwnerName() + this.not_allowed[2]);
		}
	}
	
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getBlockClicked().getLocation());
		if (claim == null) return;
		if (commonHandler(player, claim)) {
			event.setCancelled(true);
			player.sendMessage(this.not_allowed[0]);
			player.sendMessage(this.not_allowed[1] + claim.getOwnerName() + this.not_allowed[2]);
		}
	}
}
