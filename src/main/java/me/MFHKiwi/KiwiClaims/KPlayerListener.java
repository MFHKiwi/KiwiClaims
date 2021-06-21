package me.MFHKiwi.KiwiClaims;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class KPlayerListener extends PlayerListener {
	private final KiwiClaims plugin;
	private final List<KSelection> selections = new ArrayList<KSelection>();
	
	public KPlayerListener(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (selections.isEmpty()) {}
		else {
			for (Iterator<KSelection> it = selections.iterator(); it.hasNext();) {
				KSelection selection = it.next();
				if (selection.getPlayerName().equals(player.getName())) {
					if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
						selections.get(selections.indexOf(selection)).setMin(event.getClickedBlock().getLocation());
						event.getPlayer().sendMessage("Position 1 set to " + selection.getMin().getBlockX() + "X, " + selection.getMin().getBlockZ() + "Z");
					} else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						selections.get(selections.indexOf(selection)).setMax(event.getClickedBlock().getLocation());
						event.getPlayer().sendMessage("Position 2 set to " + selection.getMax().getBlockX() + "X, " + selection.getMax().getBlockZ() + "Z");
					}
					if (selection.getMin() != null && selection.getMax() != null) {
						if (selection.getMin().getWorld().equals(selection.getMax().getWorld())) {
							plugin.getClaims().add(new KClaim(selection.getMin(), selection.getMax(), selection.getPlayerName()));
							it.remove();
						} else {
							event.getPlayer().sendMessage("Selections must be in the same world!");
						}
					} else {}
				} else {}
			}
		}
	}
	
	public List<KSelection> getSelectionList() {
		return this.selections;
	}
}
