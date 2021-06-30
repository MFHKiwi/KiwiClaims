package me.MFHKiwi.KiwiClaims;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;

public class KClaimSave {
	private final KiwiClaims plugin;
	private final String value_separator = ",";
	private final File data_folder;
	private List<KClaim> claims = new ArrayList<KClaim>();
	
	public KClaimSave(KiwiClaims plugin, File data_folder) {
		this.plugin = plugin;
		this.data_folder = data_folder;
		if (!data_folder.exists()) data_folder.mkdirs();
		reloadClaims();
	}
	
	public List<KClaim> getClaimsList() {
		return this.claims;
	}
	
	public void reloadClaims() {
		File[] files_list = data_folder.listFiles();
		List<KClaim> claims = new ArrayList<KClaim>();
		for (File file : files_list) {
			try {
				claims.add(loadClaim(file));
			} catch (Exception e) {
				this.plugin.log(e.getMessage());
			}
		}
		this.claims = claims;
	}
	
	private KClaim loadClaim(File file) throws Exception {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String owner_name;
			Location min, max;
			UUID uuid;
			owner_name = in.readLine();
			min = locationFromString(in.readLine());
			max = locationFromString(in.readLine());
			uuid = UUID.fromString(in.readLine());
			in.close();
			return new KClaim(min, max, owner_name, uuid);
		} catch (Exception e) {
			throw new Exception("Could not load " + file.getName() + ": " + e.getMessage());
		}
	}
	
	public void saveClaim(KClaim claim) throws Exception {
		File file = new File(this.data_folder + File.separator + claim.getUUID().toString());
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
			out.close();
		} catch (IOException e) {
			throw new Exception("Could not save " + file.getName() + ": " + e.getMessage());
		}
	}
	
	public void addClaim(KClaim claim) throws Exception {
		saveClaim(claim);
		this.claims.add(claim);
	}
	
	public void saveClaims() {
		if (this.claims.isEmpty()) return;
		for (KClaim claim : this.claims) {
			try {
				saveClaim(claim);
			} catch (Exception e) {
				plugin.log(e.getMessage());
			}
		}
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
}
