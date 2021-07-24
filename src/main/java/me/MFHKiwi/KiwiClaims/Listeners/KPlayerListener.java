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

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.MFHKiwi.KiwiClaims.KClaim;
import me.MFHKiwi.KiwiClaims.KSelection;
import me.MFHKiwi.KiwiClaims.KiwiClaims;
import me.MFHKiwi.KiwiClaims.Utilities.KVisualisation;
import me.MFHKiwi.KiwiClaims.Utilities.KVisualisation.Type;

public class KPlayerListener extends PlayerListener {
	private final KiwiClaims plugin;
	private final List<KSelection> selections = new ArrayList<KSelection>();
	private final String internal_error, world_mismatch, overlap, claim_create, exclusion_create;
	private final String[] pos_set = new String[4];
	private final String[] claim_enter_leave = new String[3];
	
	public KPlayerListener(KiwiClaims plugin) {
		this.plugin = plugin;
		ChatColor colour1 = plugin.getColour(1);
		ChatColor colour2 = plugin.getColour(2);
		this.internal_error = colour1 + "Your claim could not be created due to an internal plugin error.";
		this.world_mismatch = colour1 + "Selections must be in the same world!";
		this.overlap = colour1 + "Your selection overlaps another claim!";
		this.claim_create = colour2 + "Claim created! Use " + colour1 + "/kc trust" + colour2 + " to allow others to build in it.";
		this.exclusion_create = colour2 + "Exclusion created.";
		this.pos_set[0] = colour2 + "Position ";
		this.pos_set[1] = colour2 + " set (" + colour1 + "x: ";
		this.pos_set[2] = colour1 + ", z: ";
		this.pos_set[3] = colour2 + ")";
		this.claim_enter_leave[0] = colour2 + "Entering " + colour1;
		this.claim_enter_leave[1] = colour2 + "Leaving " + colour1;
		this.claim_enter_leave[2] = colour2 + "'s claim.";
	}
	
	public void registerEvents() {
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.PLAYER_BED_ENTER, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.PLAYER_QUIT, (Listener) this, Event.Priority.High, (Plugin) plugin);
		pm.registerEvent(Event.Type.PLAYER_MOVE, (Listener) this, Event.Priority.High, (Plugin) plugin);
	}
	
	public void handleSelection(KSelection sel) {
		KSelection selection = sel;
		Player player = plugin.getServer().getPlayer(selection.getPlayerName());
		if (!selection.getMin().getWorld().equals(selection.getMax().getWorld())) {
			player.sendMessage(this.world_mismatch);
			return;
		}
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
			if (selection.overlaps(claim)) {
				plugin.getServer().getScheduler().scheduleAsyncDelayedTask((Plugin) plugin, new KVisualisation(player, claim, Type.ERROR));
				player.sendMessage(this.overlap);
				return;
			}
		}
		for (KClaim claim : plugin.getClaimSave().getExclusionList()) {
			if (selection.overlaps(claim)) {
				plugin.getServer().getScheduler().scheduleAsyncDelayedTask((Plugin) plugin, new KVisualisation(player, claim, Type.ERROR));
				player.sendMessage(this.overlap);
				return;
			}
		}
		try {
			KClaim claim = new KClaim(selection);
			if (selection.getExclusion()) {
				plugin.getClaimSave().addExclusion(claim);
				player.sendMessage(this.exclusion_create);
				return;
			}
			plugin.getClaimSave().addClaim(claim);
			player.sendMessage(this.claim_create);
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask((Plugin) plugin, new KVisualisation(player, claim, Type.INFO));
		} catch (Exception e) {
			plugin.log(e.getMessage());
			player.sendMessage(this.internal_error);
			return;
		}
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		Player player = event.getPlayer();
		// Bukkit somehow doesn't have an event for chest opening.
		if (event.getClickedBlock().getState() instanceof ContainerBlock && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			KClaim claim = plugin.getClaimSave().getClaimAt(event.getClickedBlock().getLocation());
			if (claim == null) return;
			if (this.plugin.shouldPrevent(player, claim)) {
				event.setCancelled(true);
			}
			return;
		}
		// And it doesn't have an adequate one for vehicle placement either...
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
				player.getItemInHand().getType().equals(Material.BOAT) ||
				(player.getItemInHand().getType().equals(Material.MINECART) &&
				(event.getClickedBlock().getType().equals(Material.RAILS) ||
				event.getClickedBlock().getType().equals(Material.DETECTOR_RAIL) ||
				event.getClickedBlock().getType().equals(Material.POWERED_RAIL)))) {
			KClaim claim = plugin.getClaimSave().getClaimAt(event.getClickedBlock().getLocation());
			if (claim == null) return;
			if (this.plugin.shouldPrevent(player, claim)) {
				event.setCancelled(true);
			}
			return;
		}
		// Nor does it have one for soil trampling...
		if (event.getClickedBlock().getType().equals(Material.SOIL) && event.getAction().equals(Action.PHYSICAL)) {
			KClaim claim = plugin.getClaimSave().getClaimAt(event.getClickedBlock().getLocation());
			if (claim == null) return;
			if (this.plugin.shouldPrevent(player, claim, true)) {
				event.setCancelled(true);
			}
			return;
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
				player.sendBlockChange(selection.getMin(), selection.getMin().getBlock().getType(), (byte) 0);
				player.sendBlockChange(selection.getMax(), selection.getMax().getBlock().getType(), (byte) 0);
				handleSelection(selection);
				it.remove();
			}
		}
	}
	
	public boolean register(Player player) {
		for (KSelection selection : this.selections) {
			if (selection.getPlayerName().equals(player.getName())) return false;
		}
		this.selections.add(new KSelection(player.getName(), false));
		return true;
	}
	
	public boolean registerExclusion(Player player) {
		for (KSelection selection : this.selections) {
			if (selection.getPlayerName().equals(player.getName())) return false;
		}
		this.selections.add(new KSelection(player.getName(), true));
		return true;
	}
	
	public boolean unregister(Player player) {
		for (Iterator<KSelection> it = this.selections.iterator(); it.hasNext();) {
			KSelection selection = it.next();
			if (selection.getPlayerName().equalsIgnoreCase(player.getName())) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		Block bed = event.getBed();
		Player player = event.getPlayer();
		KClaim claim = plugin.getClaimSave().getClaimAt(bed.getLocation());
		if (claim == null) return;
		if (this.plugin.shouldPrevent(player, claim)) {
			event.setCancelled(true);
		}
	}
	
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getBlockClicked().getLocation());
		if (claim == null) return;
		if (this.plugin.shouldPrevent(player, claim)) {
			event.setCancelled(true);
		}
	}
	
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getBlockClicked().getLocation());
		if (claim == null) return;
		if (this.plugin.shouldPrevent(player, claim)) {
			event.setCancelled(true);
		}
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		unregister(event.getPlayer());
	}
	
	public void onPlayerMove(PlayerMoveEvent event) {
		KClaim claim_at_from = plugin.getClaimSave().getClaimAt(event.getFrom());
		KClaim claim_at_to = plugin.getClaimSave().getClaimAt(event.getTo());
		Player player = event.getPlayer();
		if (claim_at_from == null && claim_at_to == null) return;
		else if (claim_at_from == null && claim_at_to != null) {
			player.sendMessage(this.claim_enter_leave[0] + claim_at_to.getOwnerName() + this.claim_enter_leave[2]);
			return;
		}
		else if (claim_at_from != null && claim_at_to == null) {
			player.sendMessage(this.claim_enter_leave[1] + claim_at_from.getOwnerName() + this.claim_enter_leave[2]);
		}
	}
}
