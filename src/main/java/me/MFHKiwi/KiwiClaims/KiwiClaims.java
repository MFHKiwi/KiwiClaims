package me.MFHKiwi.KiwiClaims;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class KiwiClaims extends JavaPlugin {
	private KClaimSave claim_save;
	private ChatColor colour1 = ChatColor.RED;
	private ChatColor colour2 = ChatColor.DARK_AQUA;
	private final KBlockListener block_listener = new KBlockListener(this);
	private final KPlayerListener player_listener = new KPlayerListener(this);
	private final KEntityListener entity_listener = new KEntityListener(this);

	
	public void onEnable() {
		File claim_folder = new File(this.getDataFolder() + File.separator + "claims");
		this.claim_save = new KClaimSave(this, claim_folder);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK, (Listener) block_listener, Event.Priority.High, (Plugin) this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, (Listener) block_listener, Event.Priority.High, (Plugin) this);
		pm.registerEvent(Event.Type.BLOCK_FROMTO, (Listener) block_listener, Event.Priority.High, (Plugin) this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, (Listener) entity_listener, Event.Priority.High, (Plugin) this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, (Listener) entity_listener, Event.Priority.High, (Plugin) this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, (Listener) player_listener, Event.Priority.High, (Plugin) this);
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
}
