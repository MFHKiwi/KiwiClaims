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

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class KiwiClaims extends JavaPlugin {
	private KClaimSave claim_save;
	private ChatColor colour1 = ChatColor.RED;
	private ChatColor colour2 = ChatColor.DARK_AQUA;
	private final KBlockListener block_listener = new KBlockListener(this);
	private final KPlayerListener player_listener = new KPlayerListener(this);
	private final KEntityListener entity_listener = new KEntityListener(this);
	private final KVehicleListener vehicle_listener = new KVehicleListener(this);

	public void onEnable() {
		File claim_folder = new File(this.getDataFolder() + File.separator + "claims");
		File exclusions_folder = new File(this.getDataFolder() + File.separator + "exclusions");
		this.claim_save = new KClaimSave(this, claim_folder, exclusions_folder);
		this.block_listener.registerEvents();
		this.entity_listener.registerEvents();
		this.player_listener.registerEvents();
		this.vehicle_listener.registerEvents();
		this.getCommand("kc").setExecutor(new KCommandHandler(this, player_listener));
		log("Plugin enabled.");
	}
	
	public void onDisable() {
		this.claim_save.saveClaims();
		log("Plugin disabled.");
	}
	
	public KClaimSave getClaimSave() {
		return this.claim_save;
	}
	
	public void log(String message) {
		Logger.getLogger("Minecraft").info("[" + this.getDescription().getFullName() + "] " + message);
	}
	
	public ChatColor getColour(int id) {
		if (id == 1) return this.colour1;
		else if (id == 2) return this.colour2;
		else return null;
	}
	
	// Common methods that classes use. Not worth creating a class for, so I'm putting it in here.
	public static boolean shouldPrevent(Player player, KClaim claim) {
		String player_name = player.getName();
		if (!claim.ownerEquals(player_name) &&
			!claim.isTrusted(player_name) &&
			!player.hasPermission("kc.admin")) return true;
		else return false;
	}
}
