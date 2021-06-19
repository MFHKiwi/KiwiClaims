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
	public PluginDescriptionFile desc;
	public PluginManager pm;
	public List<KClaim> claims = new ArrayList<KClaim>();
	
	public void onEnable() {
		this.desc = this.getDescription();
		this.pm = Bukkit.getPluginManager();
		getCommand("kc").setExecutor(new KCommand(this));
		pm.registerEvent(Event.Type.BLOCK_BREAK, (Listener) new KBlockListener(this), Event.Priority.High, (Plugin) this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, (Listener) new KPlayerListener(this), Event.Priority.High, (Plugin) this);
		log.info("[" + desc.getFullName() + "] enabled.");
	}
	
	public void onDisable() {
		log.info("[" + desc.getFullName() + "] disabled.");
	}
	
	public List<KClaim> getClaims() {
		return this.claims;
	}
}
