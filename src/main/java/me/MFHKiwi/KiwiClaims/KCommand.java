package me.MFHKiwi.KiwiClaims;

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
		Player player = (Player) sender;
		if (sender instanceof Player) {
			//if (sender.hasPermission("kc.use")) {
				if (args[0].equalsIgnoreCase("claim")) {
					listener.getSelectionList().add(new KSelection(player.getName()));
				} else if (args[0].equalsIgnoreCase("trust")) {
					int index = 0;
					for (KClaim claim : plugin.getClaims()) {
						index++;
						if (claim.contains(player.getLocation())) {
							plugin.getClaims().get(index - 1).addTrusted(args[1]);
						} else {
							
						}
					}
				}
			/*} else {
				sender.sendMessage("You need the permission node 'kc.use' to use this command.");
			}*/
			if (sender.hasPermission("kc.admin")) {
				
			} else {
				sender.sendMessage("You need the permission node 'kc.admin' to use this command.");
			}
		}
		return true;
	}
}
