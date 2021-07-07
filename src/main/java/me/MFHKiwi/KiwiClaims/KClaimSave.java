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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;

public class KClaimSave {
	private final KiwiClaims plugin;
	private final String value_separator = ",";
	private final File data_folder, exclusions_folder;
	private List<KClaim> claims = new ArrayList<KClaim>(), exclusions = new ArrayList<KClaim>();
	
	public KClaimSave(KiwiClaims plugin, File data_folder, File exclusions_folder) {
		this.plugin = plugin;
		this.data_folder = data_folder;
		this.exclusions_folder = exclusions_folder;
		if (!data_folder.exists()) data_folder.mkdirs();
		if (!exclusions_folder.exists()) exclusions_folder.mkdirs();
		reloadClaims();
	}
	
	public List<KClaim> getClaimsList() {
		return this.claims;
	}
	
	public List<KClaim> getExclusionList() {
		return this.exclusions;
	}
	
	public void reloadClaims() {
		File[] files_list = data_folder.listFiles(), exclusion_files_list = exclusions_folder.listFiles();
		List<KClaim> claims = new ArrayList<KClaim>(), exclusions = new ArrayList<KClaim>();
		int failed_claims = 0, failed_exclusions = 0;
		for (File file : files_list) {
			try {
				claims.add(loadClaim(file));
			} catch (Exception e) {
				plugin.log(e.getMessage());
				failed_claims++;
			}
		}
		for (File file : exclusion_files_list) {
			try {
				exclusions.add(loadClaim(file));
			} catch (Exception e) {
				plugin.log(e.getMessage());
				failed_exclusions++;
			}
		}
		this.claims = claims;
		this.exclusions = exclusions;
		plugin.log("Loaded " + claims.size() + " claims. " + failed_claims + " claims failed to load.");
		plugin.log("Loaded " + exclusions.size() + " exclusions. " + failed_exclusions + " exclusions failed to load.");
	}
	
	public void removeClaim(KClaim claim) {
		for (Iterator<KClaim> it = this.claims.iterator(); it.hasNext();) {
			KClaim claim_from_list = it.next();
			if (claim.getUUID().equals(claim_from_list.getUUID())) {
				it.remove();
				new File(this.data_folder + File.separator + claim.getUUID().toString()).delete();
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
	
	private KClaim loadClaim(File file) throws Exception {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String owner_name = in.readLine();
			Location min = locationFromString(in.readLine());
			Location max = locationFromString(in.readLine());
			UUID uuid = UUID.fromString(in.readLine());
			List<String> trusted = trustListFromString(in.readLine());
			in.close();
			return new KClaim(min, max, owner_name, uuid, trusted);
		} catch (Exception e) {
			throw new Exception("Could not load " + file.getName() + ": " + e.getMessage());
		}
	}
	
	private void saveClaim(KClaim claim, File folder) throws Exception {
		File file = new File(folder + File.separator + claim.getUUID().toString());
		try {
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(claim.getOwnerName());
			out.newLine();
			out.write(locationToString(claim.getMin()));
			out.newLine();
			out.write(locationToString(claim.getMax()));
			out.newLine();
			out.write(claim.getUUID().toString());
			out.newLine();
			out.write(trustListToString(claim.getTrusted()));
			out.newLine();
			out.close();
		} catch (IOException e) {
			throw new Exception("Could not save " + file.getName() + ": " + e.getMessage());
		}
	}
	
	public void addClaim(KClaim claim) throws Exception {
		this.claims.add(claim);
		try {
			saveClaim(claim, this.data_folder);
		} catch (Exception e) {
			this.claims.remove(this.claims.indexOf(claim));
			throw new Exception("Could not create claim:" + e.getMessage());
		}
	}
	
	public void addExclusion(KClaim claim) throws Exception {
		this.exclusions.add(claim);
		try {
			saveClaim(claim, this.exclusions_folder);
		} catch (Exception e) {
			this.claims.remove(this.exclusions.indexOf(claim));
			throw new Exception("Could not create exclusion:" + e.getMessage());
		}
	}
	
	public boolean addTrusted(KClaim claim, String name) throws Exception {
		int i = this.claims.indexOf(claim);
		if (this.claims.get(i) == null) throw new Exception("Claim " + claim.getUUID().toString() + " not found");
		if (claim.isTrusted(name)) return false;
		claim.addTrusted(name);
		saveClaim(claim, this.data_folder);
		this.claims.set(i, claim);
		return true;
	}
	
	public boolean removeTrusted(KClaim claim, String name) throws Exception {
		int i = this.claims.indexOf(claim);
		if (this.claims.get(i) == null) throw new Exception("Claim " + claim.getUUID().toString() + " not found");
		if (!claim.isTrusted(name)) return false;
		claim.removeTrusted(name);
		saveClaim(claim, this.data_folder);
		this.claims.set(i, claim);
		return true;
	}
	
	public void saveClaims() {
		int failed_claims = 0, failed_exclusions = 0;
		if (!this.claims.isEmpty()) {
			for (KClaim claim : this.claims) {
				try {
					saveClaim(claim, this.data_folder);
				} catch (Exception e) {
					plugin.log(e.getMessage());
					failed_claims++;
				}
			}
		}
		if (!this.exclusions.isEmpty()) {
			for (KClaim claim : this.exclusions) {
				try {
					saveClaim(claim, this.exclusions_folder);
				} catch (Exception e) {
					plugin.log(e.getMessage());
					failed_exclusions++;
				}
			}
		}
		this.plugin.log("Saved " + claims.size() + " claims. " + failed_claims + " claims failed to save.");
		this.plugin.log("Saved " + exclusions.size() + " exclusions. " + failed_exclusions + " exclusions failed to save.");
	}
	
	private Location locationFromString(String string) throws Exception {
		String[] values = string.split(this.value_separator);
		if (values.length < 4) throw new Exception("Invalid input");
		World world = plugin.getServer().getWorld(UUID.fromString(values[0]));
		if (world == null) throw new Exception("Invalid world");
		int x = Integer.parseInt(values[1]);
		int y = Integer.parseInt(values[2]);
		int z = Integer.parseInt(values[3]);
		return new Location(world, x, y, z);
	}
	
	private String locationToString(Location location) {
		StringBuilder builder = new StringBuilder(location.getWorld().getUID().toString());
		builder.append(this.value_separator);
		builder.append(location.getBlockX());
		builder.append(this.value_separator);
		builder.append(location.getBlockY());
		builder.append(this.value_separator);
		builder.append(location.getBlockZ());
		return builder.toString();
	}
	
	private List<String> trustListFromString(String string) {
		String[] elements = string.split(this.value_separator);
		List<String> trusted = new ArrayList<String>();
		for (String element : elements) {
			trusted.add(element);
		}
		return trusted;
	}
	
	private String trustListToString(List<String> trusted) {
		StringBuilder builder = new StringBuilder();
		for (String string : trusted) {
			builder.append(string + this.value_separator);
		}
		return builder.toString();
	}
	
	public KClaim getClaimAt(Location location) {
		for (KClaim claim : this.claims) {
			if (claim.contains(location)) return claim;
		}
		return null;
	}
	
	public KClaim getExclusionAt(Location location) {
		for (KClaim claim : this.exclusions) {
			if (claim.contains(location)) return claim;
		}
		return null;
	}
}
