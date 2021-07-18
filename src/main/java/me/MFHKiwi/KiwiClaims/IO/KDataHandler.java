package me.MFHKiwi.KiwiClaims.IO;

import me.MFHKiwi.KiwiClaims.KiwiClaims;
import me.MFHKiwi.KiwiClaims.Utilities.KClaimDeserializer;
import me.MFHKiwi.KiwiClaims.Utilities.KClaimSerializer;
import me.MFHKiwi.KiwiClaims.KClaim;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class KDataHandler {
	private final KiwiClaims plugin;
	private final File data_folder;
	private final ObjectMapper mapper = new ObjectMapper();
	
	public KDataHandler(KiwiClaims plugin, File data_folder) {
		this.plugin = plugin;
		if (!data_folder.exists()) data_folder.mkdirs();
		this.data_folder = data_folder;
		SimpleModule module = new SimpleModule();
		module.addSerializer(KClaim.class, new KClaimSerializer());
		module.addDeserializer(KClaim.class, new KClaimDeserializer(plugin.getServer()));
		this.mapper.registerModule(module);
	}
	
	public KClaim loadClaim(File file) throws JsonParseException, JsonMappingException, IOException {
		return this.mapper.readValue(file, KClaim.class);
	}
	
	public void saveClaim(KClaim claim) throws JsonGenerationException, JsonMappingException, IOException {
		File file = new File(this.data_folder + File.separator + claim.getUUID().toString() + ".json");
		this.mapper.writeValue(file, claim);
	}
}
