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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.MFHKiwi.KiwiClaims.IO.KDataHandler;
import me.MFHKiwi.KiwiClaims.IO.KLegacyData;
import me.MFHKiwi.KiwiClaims.Listeners.KBlockListener;
import me.MFHKiwi.KiwiClaims.Listeners.KCommandHandler;
import me.MFHKiwi.KiwiClaims.Listeners.KEntityListener;
import me.MFHKiwi.KiwiClaims.Listeners.KPlayerListener;
import me.MFHKiwi.KiwiClaims.Listeners.KVehicleListener;

public class KiwiClaims extends JavaPlugin {
	private KDataHandler claim_save;
	private final ChatColor colour1 = ChatColor.RED;
	private final ChatColor colour2 = ChatColor.DARK_AQUA;
	private final KBlockListener block_listener = new KBlockListener(this);
	private final KPlayerListener player_listener = new KPlayerListener(this);
	private final KEntityListener entity_listener = new KEntityListener(this);
	private final KVehicleListener vehicle_listener = new KVehicleListener(this);
	private final List<Player> overrides = new ArrayList<Player>();
	private final String[] not_allowed = {
			colour1 + "You are not allowed to do that here!",
			colour1 + "Ask the owner of this claim, " + colour2,
			colour1 + ", for permission.",
			colour1 + "Use " + colour2 + "/kc override" + colour1 + " to override this."
	};

	public void onEnable() {
		log("Plugin enabling...");
		KLegacyData legacy_migrator = new KLegacyData(this);
		File data_folder = new File(this.getDataFolder() + File.separator + "data");
		File legacy_claims_folder = new File(this.getDataFolder() + File.separator + "claims");
		File legacy_exclusions_folder = new File(this.getDataFolder() + File.separator + "exclusions");
		this.claim_save = new KDataHandler(this, data_folder);
		if (legacy_claims_folder.exists()) {
			log("Migrating legacy claims in '" + legacy_claims_folder.getName() + "'...");
			legacy_migrator.migrateClaims(this.claim_save, legacy_claims_folder);
			legacy_claims_folder.renameTo(new File(this.getDataFolder() + File.separator + "old_claims"));
		}
		if (legacy_exclusions_folder.exists()) {
			log("Migrating legacy claims in '" + legacy_exclusions_folder.getName() + "'...");
			legacy_migrator.migrateClaims(this.claim_save, legacy_exclusions_folder);
			legacy_exclusions_folder.renameTo(new File(this.getDataFolder() + File.separator + "old_exclusions"));
		}
		legacy_claims_folder = null;
		legacy_exclusions_folder = null;
		legacy_migrator = null;
		log("Loading claims from '" + data_folder.getName() + "'...");
		this.claim_save.loadClaims();
		this.block_listener.registerEvents();
		this.entity_listener.registerEvents();
		this.player_listener.registerEvents();
		this.vehicle_listener.registerEvents();
		this.getCommand("kc").setExecutor(new KCommandHandler(this, this.player_listener));
		log("Plugin enabled.");
		log("This plugin is licensed under the GNU General Public License v3. A copy of the license is included in the plugin Jar.");
	}
	
	public void onDisable() {
		this.claim_save.saveClaims();
		log("Plugin disabled.");
	}
	
	public KDataHandler getClaimSave() {
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
	
	public List<Player> getOverrideList() {
		return this.overrides;
	}
	
	public boolean isOverriding(Player player) {
		for (Player player_from_list : this.overrides) {
			if (player == player_from_list) {
				return true;
			}
		}
		return false;
	}
	
	// Common methods that classes use. Not worth creating a class for, so I'm putting it in here.
	public boolean shouldPrevent(Player player, KClaim claim, boolean silent) {
		String player_name = player.getName();
		boolean can_build = (claim.ownerEquals(player_name) || claim.isTrusted(player_name));
		boolean is_admin = player.hasPermission("kc.admin");
		boolean is_overriding = isOverriding(player);
		if (!can_build && !is_admin) {
			if (!silent) {
				player.sendMessage(this.not_allowed[0]);
				player.sendMessage(this.not_allowed[1] + claim.getOwnerName() + this.not_allowed[2]);
			}
			return true;
		}
		else if (!can_build && is_admin && !is_overriding) {
			if (!silent) {
				player.sendMessage(this.not_allowed[0]);
				player.sendMessage(this.not_allowed[1] + claim.getOwnerName() + this.not_allowed[2]);
				player.sendMessage(this.not_allowed[3]);
			}
			return true;
		}
		return false;
	}
	
	public boolean shouldPrevent(Player player, KClaim claim) {
		return shouldPrevent(player, claim, false);
	}
}
