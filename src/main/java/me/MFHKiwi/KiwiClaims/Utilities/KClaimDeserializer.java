package me.MFHKiwi.KiwiClaims.Utilities;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Server;

import me.MFHKiwi.KiwiClaims.KClaim;

public class KClaimDeserializer extends StdDeserializer<KClaim> {
	private static final long serialVersionUID = 1L;
	private final ObjectMapper mapper = new ObjectMapper();
	
	public KClaimDeserializer(Server server) {
		this(null, server);
	}
	
	private KClaimDeserializer(Class<KClaim> t, Server server) {
		super(t);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Location.class, new KLocationDeserializer(server));
		this.mapper.registerModule(module);
	}

	@Override
	public KClaim deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		Location min = this.mapper.treeToValue(node.get("min"), Location.class);
		Location max = this.mapper.treeToValue(node.get("max"), Location.class);
		String owner_name = node.get("owner").asText();
		List<String> trusted = this.mapper.treeToValue(node.get("trusted"), ArrayList.class);
		UUID uuid = UUID.fromString(node.get("uuid").asText());
		return new KClaim(min, max, owner_name, uuid, trusted);
	}
}
