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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.MFHKiwi.KiwiClaims.KClaim;
import me.MFHKiwi.KiwiClaims.KiwiClaims;
import me.MFHKiwi.KiwiClaims.Utilities.KVisualisation;
import me.MFHKiwi.KiwiClaims.Utilities.KVisualisation.Type;

public class KCommandHandler implements CommandExecutor {
	private final KiwiClaims plugin;
	private final KPlayerListener listener;
	private final String[] help_message = new String[12];
	private final String[] owner_set = new String[2];
	private final String[] plugin_info = new String[2];
	private final String incorrect_usage, not_player, claim_message, not_in_claim, unclaim_message, not_allowed, 
	trust_message, already_trusted, untrust_message, already_untrusted, internal_error, no_permission, already_owner, selection_cancelled, no_selection, overriding, not_overriding;
	
	
	public KCommandHandler(KiwiClaims plugin, KPlayerListener listener) {
		this.plugin = plugin;
		this.listener = listener;
		ChatColor colour1 = plugin.getColour(1);
		ChatColor colour2 = plugin.getColour(2);
		this.help_message[0] = colour2 + "-=-" + colour1 + "KiwiClaims Help" + colour2 + "-=-";
		this.help_message[1] = colour2 + " - " + colour1 + "/kc help" + colour2 + ": Show this help screen";
		this.help_message[2] = colour2 + " - " + colour1 + "/kc info" + colour2 + ": Show plugin info";
		this.help_message[3] = colour2 + " - " + colour1 + "/kc claim" + colour2 + ": Claim an area";
		this.help_message[4] = colour2 + " - " + colour1 + "/kc cancel" + colour2 + ": Cancel claim selection";
		this.help_message[5] = colour2 + " - " + colour1 + "/kc unclaim/remove" + colour2 + ": Remove claim from area";
		this.help_message[6] = colour2 + " - " + colour1 + "/kc trust <player name>" + colour2 + ": Trust player in claim";
		this.help_message[7] = colour2 + " - " + colour1 + "/kc untrust <player name>" + colour2 + ": Untrust player in claim";
		this.help_message[8] = colour2 + " - " + colour1 + "/kc exclude" + colour2 + ": Create exclusion zone";
		this.help_message[9] = colour2 + " - " + colour1 + "/kc unexclude" + colour2 + ": Remove exclusion zone";
		this.help_message[10] = colour2 + " - " + colour1 + "/kc visualise/vis" + colour2 + ": Visualise claim corners";
		this.help_message[11] = colour2 + " - " + colour1 + "/kc override" + colour2 + ": Override claims";
		this.incorrect_usage = colour1 + "Incorrect usage. See " + colour2 + "/kc help" + colour1 + ".";
		this.plugin_info[0] = colour1 + plugin.getDescription().getFullName() + colour2 + " by MFHKiwi";
		this.plugin_info[1] = colour2 + "This plugin is licensed under the " + colour1 + "GNU GPL v3" + colour2 + ".";
		this.not_player = colour1 + "You must be a player to run this command.";
		this.claim_message = colour2 + "Select a claim by left and right clicking its opposite corners.";
		this.not_in_claim = colour1 + "You must be standing in a claim to do that.";
		this.unclaim_message = colour2 + "Claim removed.";
		this.not_allowed = colour1 + "You must be owner of the claim to do that.";
		this.trust_message = colour2 + "Player trusted.";
		this.already_trusted = colour1 + "That player is already trusted in this claim.";
		this.untrust_message = colour2 + "Player untrusted.";
		this.already_untrusted = colour1 + "That player does not have trust in this claim.";
		this.internal_error = colour1 + "Could not do this due to an internal plugin error. Contact staff about this.";
		this.no_permission = colour1 + "You do not have permission to do that.";
		this.already_owner = colour1 + "You already own this claim.";
		this.owner_set[0] = colour2 + "Transferred claim to " + colour1;
		this.owner_set[1] = colour2 + ".";
		this.selection_cancelled = colour2 + "Selection cancelled.";
		this.no_selection = colour1 + "You have not started a selection.";
		this.overriding = colour2 + "Overriding claims.";
		this.not_overriding = colour2 + "No longer overriding claims.";
	}
	
	private boolean shouldPrevent(Player player, KClaim claim) {
		if (!claim.ownerEquals(player.getName()) && !player.hasPermission("kc.admin")) return true;
		return false;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(this.incorrect_usage);
			return true;
		}
		String subcommand = args[0];
		if (subcommand.equalsIgnoreCase("info")) {
			for (String string : this.plugin_info) {
				sender.sendMessage(string);
			}
			return true;
		}
		if (!sender.hasPermission("kc.use")) {
			sender.sendMessage(this.no_permission);
			return true;
		}
		if (subcommand.equalsIgnoreCase("help")) {
			for (String line : this.help_message) {
				sender.sendMessage(line);
			}
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(not_player);
			return true;
		}
		Player player = (Player) sender;
		if (subcommand.equalsIgnoreCase("claim")) {
			if (listener.register(player)) {
				player.sendMessage(this.claim_message);
			}
			return true;
		}
		if (subcommand.equalsIgnoreCase("unclaim") || subcommand.equalsIgnoreCase("remove")) {
			KClaim claim = plugin.getClaimSave().getClaimAt(player.getLocation());
			if (claim == null) {
				player.sendMessage(this.not_in_claim);
			}
			else if (shouldPrevent(player, claim)) {
				player.sendMessage(this.not_allowed);
			}
			else {
				plugin.getClaimSave().removeClaim(claim);
				player.sendMessage(this.unclaim_message);
			}
			return true;
		}
		if (subcommand.equalsIgnoreCase("trust")) {
			KClaim claim = plugin.getClaimSave().getClaimAt(player.getLocation());
			if (args.length < 2) {
				player.sendMessage(this.incorrect_usage);
			}
			else if (claim == null) {
				player.sendMessage(this.not_in_claim);
			}
			else if (shouldPrevent(player, claim)) {
				player.sendMessage(this.not_allowed);
			}
			else try {
				if (!plugin.getClaimSave().addTrusted(claim, args[1])) {
					player.sendMessage(this.already_trusted);
				} else {
					player.sendMessage(this.trust_message);
				}
			} catch (Exception e) {
				plugin.log(e.getMessage());
				player.sendMessage(this.internal_error);
			}
			return true;
		}
		if (subcommand.equalsIgnoreCase("untrust")) {
			KClaim claim = plugin.getClaimSave().getClaimAt(player.getLocation());
			if (args.length < 2) {
				player.sendMessage(this.incorrect_usage);
			}
			else if (claim == null) {
				player.sendMessage(this.not_in_claim);
			}
			else if (shouldPrevent(player, claim)) {
				player.sendMessage(this.not_allowed);
			}
			else try {
				if (!plugin.getClaimSave().removeTrusted(claim, args[1])) {
					player.sendMessage(this.already_untrusted);
				} else {
					player.sendMessage(this.untrust_message);
				}
			} catch (Exception e) {
				plugin.log(e.getMessage());
				player.sendMessage(this.internal_error);
			}
			return true;
		}
		if (subcommand.equalsIgnoreCase("transfer")) {
			KClaim claim = plugin.getClaimSave().getClaimAt(player.getLocation());
			if (args.length < 2) {
				player.sendMessage(this.incorrect_usage);
			}
			else if (claim == null) {
				player.sendMessage(this.not_in_claim);
			}
			else if (shouldPrevent(player, claim)) {
				player.sendMessage(this.not_allowed);
			}
			else try {
				if (!plugin.getClaimSave().setOwner(claim, args[1])) {
					player.sendMessage(this.already_owner);
				} else {
					player.sendMessage(this.owner_set[0] + args[1] + this.owner_set[1]);
				}
			} catch (Exception e) {
				plugin.log(e.getMessage());
				player.sendMessage(this.internal_error);
			}
			return true;
		}
		if (subcommand.equalsIgnoreCase("visualise") || subcommand.equalsIgnoreCase("vis")) {
			KClaim claim = plugin.getClaimSave().getClaimAt(player.getLocation());
			if (claim == null) {
				player.sendMessage(this.not_in_claim);
			}
			else {
				plugin.getServer().getScheduler().scheduleAsyncDelayedTask((Plugin) plugin, new KVisualisation(player, claim, Type.INFO));
			}
			return true;
		}
		if (subcommand.equalsIgnoreCase("cancel")) {
			if (!listener.unregister(player)) {
				player.sendMessage(this.no_selection);
			} else {
				player.sendMessage(this.selection_cancelled);
			}
			return true;
		}
		if (!player.hasPermission("kc.admin")) {
			player.sendMessage(this.no_permission);
			return true;
		}
		if (subcommand.equalsIgnoreCase("exclude")) {
			if (listener.registerExclusion(player)) {
				player.sendMessage(this.claim_message);
			}
			return true;
		}
		if (subcommand.equalsIgnoreCase("unexclude")) {
			KClaim claim = plugin.getClaimSave().getExclusionAt(player.getLocation());
			if (claim == null) {
				player.sendMessage(this.not_in_claim);
			}
			else {
				plugin.getClaimSave().removeExclusion(claim);
				player.sendMessage(this.unclaim_message);
			}
			return true;
		}
		if (subcommand.equalsIgnoreCase("override")) {
			if (!plugin.isOverriding(player)) {
				plugin.getOverrideList().add(player);
				player.sendMessage(this.overriding);
			}
			else for (Iterator<Player> it = plugin.getOverrideList().iterator(); it.hasNext();) {
				if (it.next() == player) {
					it.remove();
					player.sendMessage(not_overriding);
				}
			}
			return true;
		}
		sender.sendMessage(this.incorrect_usage);
		return true;
	}

}
