package me.MFHKiwi.KiwiClaims;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KCommandHandler implements CommandExecutor {
	private final KiwiClaims plugin;
	private final KPlayerListener listener;
	private final ChatColor colour1;
	private final ChatColor colour2;
	private final String[] help_message = new String[7];
	private final String incorrect_usage, plugin_info, not_player, claim_message, not_in_claim, unclaim_message, not_allowed, 
	trust_message, already_trusted, untrust_message, already_untrusted, internal_error;
	
	public KCommandHandler(KiwiClaims plugin, KPlayerListener listener) {
		this.plugin = plugin;
		this.listener = listener;
		this.colour1 = plugin.getColour(1);
		this.colour2 = plugin.getColour(2);
		this.help_message[0] = colour2 + "-=-" + colour1 + "KiwiClaims Help" + colour2 + "-=-";
		this.help_message[1] = colour2 + " - " + colour1 + "/kc help" + colour2 + ": Show this help screen";
		this.help_message[2] = colour2 + " - " + colour1 + "/kc info" + colour2 + ": Show plugin info";
		this.help_message[3] = colour2 + " - " + colour1 + "/kc claim" + colour2 + ": Claim an area";
		this.help_message[4] = colour2 + " - " + colour1 + "/kc unclaim/remove" + colour2 + ": Remove claim from area";
		this.help_message[5] = colour2 + " - " + colour1 + "/kc trust <player name>" + colour2 + ": Trust player in claim";
		this.help_message[6] = colour2 + " - " + colour1 + "/kc untrust <player name>" + colour2 + ": Untrust player in claim";
		this.incorrect_usage = colour2 + "Incorrect usage. See " + colour1 + "/kc help" + colour2 + ".";
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
	}
	
	private boolean commonHandler(Player player, KClaim claim) {
		if (claim.getOwnerName().equals(player.getName()) || player.hasPermission("kc.admin")) return true;
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
		else if (subcommand.equalsIgnoreCase("help")) {
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
			listener.getSelectionList().add(new KSelection(player.getName()));
			player.sendMessage(this.claim_message);
		}
		else if (subcommand.equalsIgnoreCase("unclaim") || subcommand.equalsIgnoreCase("remove")) {
			KClaim claim = plugin.getClaimSave().getClaimAt(player.getLocation());
			if (claim == null) {
				player.sendMessage(this.not_in_claim);
				return true;
			}
			if (!commonHandler(player, claim)) {
				player.sendMessage(this.not_allowed);
			}
			else {
				plugin.getClaimSave().removeClaim(claim);
				player.sendMessage(this.unclaim_message);
			}
		}
		else if (subcommand.equalsIgnoreCase("trust")) {
			KClaim claim = plugin.getClaimSave().getClaimAt(player.getLocation());
			if (claim == null) {
				player.sendMessage(this.not_in_claim);
				return true;
			}
			try {
				if (!plugin.getClaimSave().addTrusted(claim, args[1])) {
					player.sendMessage(this.already_trusted);
				} else {
					player.sendMessage(this.trust_message);
				}
			} catch (Exception e) {
				plugin.log(e.getMessage());
				player.sendMessage(this.internal_error);
			}
		}
		else if (subcommand.equalsIgnoreCase("untrust")) {
			KClaim claim = plugin.getClaimSave().getClaimAt(player.getLocation());
			if (claim == null) {
				player.sendMessage(this.not_in_claim);
				return true;
			}
			try {
				if (!plugin.getClaimSave().removeTrusted(claim, args[1])) {
					player.sendMessage(this.already_untrusted);
				} else {
					player.sendMessage(this.untrust_message);
				}
			} catch (Exception e) {
				plugin.log(e.getMessage());
				player.sendMessage(this.internal_error);
			}
		}
		return true;
	}

}
