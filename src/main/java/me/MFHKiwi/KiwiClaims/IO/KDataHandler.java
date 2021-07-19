package me.MFHKiwi.KiwiClaims.IO;

import me.MFHKiwi.KiwiClaims.KiwiClaims;
import me.MFHKiwi.KiwiClaims.Utilities.KClaimDeserializer;
import me.MFHKiwi.KiwiClaims.Utilities.KClaimSerializer;
import me.MFHKiwi.KiwiClaims.KClaim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class KDataHandler {
	private final KiwiClaims plugin;
	private final File claims_folder, exclusions_folder;
	private final ObjectMapper mapper = new ObjectMapper();
	private List<KClaim> claims = new ArrayList<KClaim>(), exclusions = new ArrayList<KClaim>();
	
	public KDataHandler(KiwiClaims plugin, File claims_folder, File exclusions_folder) {
		this.plugin = plugin;
		if (!claims_folder.exists()) claims_folder.mkdirs();
		if (!exclusions_folder.exists()) exclusions_folder.mkdirs();
		this.claims_folder = claims_folder;
		this.exclusions_folder = exclusions_folder;
		SimpleModule module = new SimpleModule();
		module.addSerializer(KClaim.class, new KClaimSerializer());
		module.addDeserializer(KClaim.class, new KClaimDeserializer(plugin.getServer()));
		this.mapper.registerModule(module);
		this.claims = loadList(this.claims_folder);
		this.exclusions = loadList(this.exclusions_folder);
		if (this.claims == null) this.claims = new ArrayList<KClaim>();
		if (this.exclusions == null) this.exclusions = new ArrayList<KClaim>();
	}
	
	public List<KClaim> getClaimsList() {
		return this.claims;
	}
	
	public List<KClaim> getExclusionList() {
		return this.exclusions;
	}
	
	public void saveClaims() {
		saveList(this.claims, this.claims_folder);
		saveList(this.exclusions, this.exclusions_folder);
	}
	
	public KClaim getClaimAt(Location location) {
		if (this.claims.isEmpty()) return null;
		for (KClaim claim : this.claims) {
			if (claim.contains(location)) return claim;
		}
		return null;
	}
	
	public KClaim getExclusionAt(Location location) {
		if (this.claims.isEmpty()) return null;
		for (KClaim claim : this.exclusions) {
			if (claim.contains(location)) return claim;
		}
		return null;
	}
	
	public void addClaim(KClaim claim) {
		this.claims.add(claim);
		try {
			saveClaim(claim, this.claims_folder);
		} catch (Exception e) {
			this.claims.remove(this.claims.indexOf(claim));
			this.plugin.log("Could not save claim" + claim.getUUID().toString() + ": " + e.getMessage());
		}
	}
	
	public void addExclusion(KClaim claim) {
		this.exclusions.add(claim);
		try {
			saveClaim(claim, this.exclusions_folder);
		} catch (Exception e) {
			this.exclusions.remove(this.claims.indexOf(claim));
			this.plugin.log("Could not save exclusion" + claim.getUUID().toString() + ": " + e.getMessage());
		}
	}
	
	public void removeClaim(KClaim claim) {
		for (Iterator<KClaim> it = this.claims.iterator(); it.hasNext();) {
			KClaim claim_from_list = it.next();
			if (claim.getUUID().equals(claim_from_list.getUUID())) {
				it.remove();
				new File(this.claims_folder + File.separator + claim.getUUID().toString() + "").delete();
			}
		}
	}
	
	public void removeExclusion(KClaim claim) {
		for (Iterator<KClaim> it = this.exclusions.iterator(); it.hasNext();) {
			KClaim claim_from_list = it.next();
			if (claim.getUUID().equals(claim_from_list.getUUID())) {
				it.remove();
				new File(this.exclusions_folder + File.separator + claim.getUUID().toString()).delete();
			}
		}
	}
	
	public boolean addTrusted(KClaim claim, String name) throws Exception {
		int i = this.claims.indexOf(claim);
		if (this.claims.get(i) == null) throw new Exception("Claim " + claim.getUUID().toString() + " not found");
		if (claim.isTrusted(name)) return false;
		claim.addTrusted(name);
		saveClaim(claim, this.claims_folder);
		this.claims.set(i, claim);
		return true;
	}
	
	public boolean removeTrusted(KClaim claim, String name) throws Exception {
		int i = this.claims.indexOf(claim);
		if (this.claims.get(i) == null) throw new Exception("Claim " + claim.getUUID().toString() + " not found");
		if (!claim.isTrusted(name)) return false;
		claim.removeTrusted(name);
		saveClaim(claim, this.claims_folder);
		this.claims.set(i, claim);
		return true;
	}
	
	public boolean setOwner(KClaim claim, String name) throws Exception {
		int i = this.claims.indexOf(claim);
		if (this.claims.get(i) == null) throw new Exception("Claim " + claim.getUUID().toString() + " not found");
		if (claim.ownerEquals(name)) return false;
		claim.setOwnerName(name);
		saveClaim(claim, this.claims_folder);
		this.claims.set(i, claim);
		return true;
	}
	
	private List<KClaim> loadList(File folder) {
		List<KClaim> claims = new ArrayList<KClaim>();
		File[] file_list = folder.listFiles();
		if (file_list.length == 0) return null;
		int failed_claims = 0;
		for (File file : file_list) {
			try {
				claims.add(loadClaim(file));
			} catch (Exception e) {
				this.plugin.log("Could not load claim " + folder.getName() + File.separator + file.getName() + ": " + e.getMessage());
				failed_claims++;
			}
		}
		this.plugin.log("Loaded " + claims.size() + " claims in folder " + folder.getName() + ". " + failed_claims + " claims failed to load.");
		return claims;
	}
	
	private void saveList(List<KClaim> claims, File folder) {
		int failed_claims = 0;
		if (claims.isEmpty()) return;
		for (KClaim claim : claims) {
			try {
				saveClaim(claim, folder);
			} catch (Exception e) {
				this.plugin.log("Could not save claim " + folder.getName() + File.separator + claim.getUUID() + ".json: " + e.getMessage());
				failed_claims++;
			}
		}
		this.plugin.log("Saved " + claims.size() + " claims in folder " + folder.getName() + ". " + failed_claims + " claims failed to load.");
	}
	
	private KClaim loadClaim(File file) throws JsonParseException, JsonMappingException, IOException {
		return this.mapper.readValue(file, KClaim.class);
	}
	
	private void saveClaim(KClaim claim, File folder) throws JsonGenerationException, JsonMappingException, IOException {
		File file = new File(folder + File.separator + claim.getUUID().toString() + ".json");
		this.mapper.writeValue(file, claim);
	}
}
