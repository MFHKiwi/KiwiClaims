package me.MFHKiwi.KiwiClaims.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;

import me.MFHKiwi.KiwiClaims.KClaim;
import me.MFHKiwi.KiwiClaims.KiwiClaims;

public class KLegacyData {
	private final KiwiClaims plugin;
	private final String value_separator = ",";
	
	public KLegacyData(KiwiClaims plugin) {
		this.plugin = plugin;
	}
	
	public void migrateClaims(KDataHandler new_data, File folder) {
		int migrated_claims = 0, failed_claims = 0;
		File[] file_list = folder.listFiles();
		for (File file : file_list) {
			try {
				new_data.addClaim(loadClaim(file));
				migrated_claims++;
			} catch (Exception e) {
				plugin.log("Failed to migrate '" + file.getName() + "': " + e.getMessage());
				failed_claims++;
			}
		}
		this.plugin.log("Migrated " + migrated_claims + " legacy claims from '" + folder.getName() + "'. " + failed_claims + " failed to migrate.");
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
	
	private List<String> trustListFromString(String string) {
		String[] elements = string.split(this.value_separator);
		List<String> trusted = new ArrayList<String>();
		for (String element : elements) {
			trusted.add(element);
		}
		return trusted;
	}
}
