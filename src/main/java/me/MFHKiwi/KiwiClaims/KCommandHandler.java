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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KCommandHandler implements CommandExecutor {
	private final KiwiClaims plugin;
	private final KPlayerListener listener;
	private final String[] help_message = new String[7];
	private final String incorrect_usage, plugin_info, not_player, claim_message, not_in_claim, unclaim_message, not_allowed, 
	trust_message, already_trusted, untrust_message, already_untrusted, internal_error, no_permission;
	
	public KCommandHandler(KiwiClaims plugin, KPlayerListener listener) {
		this.plugin = plugin;
		this.listener = listener;
		ChatColor colour1 = plugin.getColour(1);
		ChatColor colour2 = plugin.getColour(2);
		this.help_message[0] = colour2 + "-=-" + colour1 + "KiwiClaims Help" + colour2 + "-=-";
		this.help_message[1] = colour2 + " - " + colour1 + "/kc help" + colour2 + ": Show this help screen";
		this.help_message[2] = colour2 + " - " + colour1 + "/kc info" + colour2 + ": Show plugin info";
		this.help_message[3] = colour2 + " - " + colour1 + "/kc claim" + colour2 + ": Claim an area";
		this.help_message[4] = colour2 + " - " + colour1 + "/kc unclaim/remove" + colour2 + ": Remove claim from area";
		this.help_message[5] = colour2 + " - " + colour1 + "/kc trust <player name>" + colour2 + ": Trust player in claim";
		this.help_message[6] = colour2 + " - " + colour1 + "/kc untrust <player name>" + colour2 + ": Untrust player in claim";
		this.incorrect_usage = colour1 + "Incorrect usage. See " + colour2 + "/kc help" + colour1 + ".";
		this.plugin_info = colour1 + plugin.getDescription().getFullName() + colour2 + " by MFHKiwi";
		this.not_player = colour1 + "You must be a player to run this command.";
		this.claim_message = colour2 + "Punch the opposite corners of the claim you with to create.";
		this.not_in_claim = colour1 + "You must be standing in a claim to do that.";
		this.unclaim_message = colour2 + "Claim removed.";
		this.not_allowed = colour1 + "You must be owner of the claim to do that";
		this.trust_message = colour2 + "Player trusted.";
		this.already_trusted = colour1 + "That player is already trusted in this claim.";
		this.untrust_message = colour2 + "Player untrusted.";
		this.already_untrusted = colour1 + "That player does not have trust in this claim.";
		this.internal_error = colour1 + "Could not do this due to an internal plugin error. Contact staff about this.";
		this.no_permission = colour1 + "You do not have permission to do that.";
	}
	
	private boolean shouldPrevent(Player player, KClaim claim) {
		if (!claim.getOwnerName().equals(player.getName()) && !player.hasPermission("kc.admin")) return true;
		return false;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(this.incorrect_usage);
			return true;
		}
		String subcommand = args[0];
		if (subcommand.equalsIgnoreCase("info")) {
			sender.sendMessage(this.plugin_info);
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
			if (!listener.register(player)) return true;
			player.sendMessage(this.claim_message);
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
		sender.sendMessage(this.incorrect_usage);
		return true;
	}

}
