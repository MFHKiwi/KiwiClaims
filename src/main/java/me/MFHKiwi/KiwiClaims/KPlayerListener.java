package me.MFHKiwi.KiwiClaims;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.block.ContainerBlock;
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
				player.sendMessage("Position 1 set to " + selection.getMin().getBlockX() + "X, " + selection.getMin().getBlockZ() + "Z");
			}
			else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				selections.get(selections.indexOf(selection)).setMax(event.getClickedBlock().getLocation());
				player.sendMessage("Position 2 set to " + selection.getMax().getBlockX() + "X, " + selection.getMax().getBlockZ() + "Z");
			}
			if (selection.getMin() != null && selection.getMax() != null) {
				switch (handleSelection(selection)) {
					case 3:
						player.sendMessage("Your claim could not be created due to an internal plugin error.");
						break;
					case 2:
						player.sendMessage("Selections must be in the same world!");
						break;
					case 1:
						player.sendMessage("Your selection overlaps another claim!");
						break;
					case 0:
						player.sendMessage("Claim created!");
						break;
				}
				it.remove();
			}
		}
	}
	
	public List<KSelection> getSelectionList() {
		return this.selections;
	}
}
