package me.MFHKiwi.KiwiClaims;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class KEntityListener extends EntityListener {
	private final KiwiClaims plugin;
	
	public KEntityListener(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) return;
		if (!(event.getEntity() instanceof LivingEntity)) return;
		if (!(event.getEntity() instanceof Animals)) return;
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getEntity().getLocation());
		EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
		if (claim == null) return;
		Entity damager = event2.getDamager();
		if (!(damager instanceof Player)) {
			event.setCancelled(true);
		}
		Player player = (Player) event2.getDamager();
		String player_name = player.getName();
		if (!player_name.equals(claim.getOwnerName())) {
			for (String trusted_name : claim.getTrusted()) {
				if (player_name.equals(trusted_name)) return;
			}
			event.setCancelled(true);
		}
	}
	
	public void onEntityExplode(EntityExplodeEvent event) {
		KClaim claim = plugin.getClaimSave().getClaimAt(event.getLocation());
		if (claim != null) {
			event.setCancelled(true);
		}
	}
}
