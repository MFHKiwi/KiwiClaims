package me.MFHKiwi.KiwiClaims;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class KSave {
	private final KiwiClaims plugin;
	
	public KSave(KiwiClaims plugin, File data_folder) {
		this.plugin = plugin;
		if (!data_folder.exists()) data_folder.mkdirs();
		File[] files_list = data_folder.listFiles();
		for (File file : files_list) {
			if (file.getName().split(".")[1].equals("claim")) {
				loadClaim(file);
			}
		}
	}
	
	public KClaim loadClaim(File file) {
		return new KClaim(null, null, null);
	}
	
	public void saveClaim(KClaim claim) {
		
	}
}
