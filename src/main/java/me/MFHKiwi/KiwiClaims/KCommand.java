package me.MFHKiwi.KiwiClaims;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KCommand implements CommandExecutor {
	private final KiwiClaims plugin;
	
	public KCommand(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		if (sender instanceof Player) {
			if (sender.hasPermission("kc.use")) {
				if (args[0].equalsIgnoreCase("claim")) {
					//plugin.getClaims().add(new KClaim(0, 0, 128, 128, player, player.getWorld()));
					
				}
			} else {
				sender.sendMessage("You need the permission node 'kc.use' to use this command.");
			}
			if (sender.hasPermission("kc.admin")) {
				
			} else {
				sender.sendMessage("You need the permission node 'kc.admin' to use this command.");
			}
		}
		return true;
	}
}
