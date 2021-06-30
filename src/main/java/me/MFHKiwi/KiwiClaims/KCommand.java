package me.MFHKiwi.KiwiClaims;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KCommand implements CommandExecutor {
	private final KiwiClaims plugin;
	private final KPlayerListener listener;
	
	public KCommand(KiwiClaims plugin, KPlayerListener listener) {
		this.plugin = plugin;
		this.listener = listener;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			List<KClaim> claims = plugin.getClaimSave().getClaimsList();
			if (sender.hasPermission("kc.use")) {
				if (args.length < 1) {
					sender.sendMessage("Incorrect usage. See at /kc help.");
					return true;
				}
				if (args[0].equalsIgnoreCase("help" )) {
					
				}
				if (args[0].equalsIgnoreCase("claim")) {
					listener.getSelectionList().add(new KSelection(player.getName()));
					sender.sendMessage("Punch the opposite corners of the claim you wish to create in order to make your selection");
				} else if (args[0].equalsIgnoreCase("trust")) {
					for (KClaim claim : claims) {
						if (claim.contains(player.getLocation())) {
							if (claim.getOwnerName().equals(player.getName()) || sender.hasPermission("kc.admin")) {
								if (claims.get(claims.indexOf(claim)).addTrusted(args[1])) {
									sender.sendMessage("Successfully trusted");
								} else {
									sender.sendMessage("That player is already trusted in this claim");
								}
							} else {
								sender.sendMessage("You must be owner of the claim to do that");
							}
						} else {
							sender.sendMessage("You must be standing in a claim to do that");
						}
					}
				} else if (args[0].equalsIgnoreCase("untrust")) {
					for (KClaim claim : claims) {
						if (claim.contains(player.getLocation())) {
							if (claim.getOwnerName().equals(player.getName())) {
								if (claims.get(claims.indexOf(claim)).removeTrusted(args[1])) {
									sender.sendMessage("Successfully untrusted");
								} else {
									sender.sendMessage("That player is not trusted in this claim");
								}
							} else {
								sender.sendMessage("You must be owner of the claim to do that");
							}
						} else {
							sender.sendMessage("You must be standing in a claim to do that");
						}
					}
				}
			} else {
				sender.sendMessage("You need the permission node 'kc.use' to use this command.");
			}
		} else {
			sender.sendMessage("This command can only be used in-game");
		}
		return true;
	}
}
