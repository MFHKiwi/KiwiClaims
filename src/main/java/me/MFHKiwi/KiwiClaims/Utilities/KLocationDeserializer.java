package me.MFHKiwi.KiwiClaims.Utilities;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class KLocationDeserializer extends StdDeserializer<Location> {
	private static final long serialVersionUID = 1L;
	private final Server server;
	
	public KLocationDeserializer(Server server) {
		this(null, server);
	}
	
	private KLocationDeserializer(Class<Location> t, Server server) {
		super(t);
		this.server = server;
	}

	@Override
	public Location deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		double x = node.get("x").asDouble();
		double y = node.get("y").asDouble();
		double z = node.get("z").asDouble();
		float pitch = node.get("pitch").floatValue();
		float yaw = node.get("yaw").floatValue();
		UUID world_uuid = UUID.fromString(node.get("world_uuid").asText());
		return new Location(this.server.getWorld(world_uuid), x, y, z, pitch, yaw);
	}	
}
