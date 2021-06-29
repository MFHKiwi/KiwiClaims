package me.MFHKiwi.KiwiClaims;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class KiwiClaims extends JavaPlugin {
	public final Logger log = Logger.getLogger("Minecraft");
	private PluginDescriptionFile desc;
	private PluginManager pm;
	private List<KClaim> claims = new ArrayList<KClaim>();
	private final KBlockListener block_listener = new KBlockListener(this);
	private final KPlayerListener player_listener = new KPlayerListener(this);
	
	public void onEnable() {
		this.desc = this.getDescription();
		this.pm = Bukkit.getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK, (Listener) block_listener, Event.Priority.High, (Plugin) this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, (Listener) block_listener, Event.Priority.High, (Plugin) this);
		pm.registerEvent(Event.Type.BLOCK_FROMTO, (Listener) block_listener, Event.Priority.High, (Plugin) this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, (Listener) player_listener, Event.Priority.High, (Plugin) this);
		this.getCommand("kc").setExecutor(new KCommand(this, player_listener));
		log.info("[" + desc.getFullName() + "] enabled.");
	}
	
	public void onDisable() {
		log.info("[" + desc.getFullName() + "] disabled.");
	}
	
	public List<KClaim> getClaims() {
		return this.claims;
	}
}
