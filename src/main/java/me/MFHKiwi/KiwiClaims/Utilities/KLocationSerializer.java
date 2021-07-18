package me.MFHKiwi.KiwiClaims.Utilities;

import java.io.IOException;

import org.bukkit.Location;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class KLocationSerializer extends StdSerializer<Location> {
	private static final long serialVersionUID = 1L;
	
	public KLocationSerializer() {
		this(null);
	}
	
	private KLocationSerializer(Class<Location> t) {
		super(t);
	}

	@Override
	public void serialize(Location value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.useDefaultPrettyPrinter();
		gen.writeStartObject();
		gen.writeFieldName("x");
		gen.writeRawValue(String.valueOf(value.getX()));
		gen.writeFieldName("y");
		gen.writeRawValue(String.valueOf(value.getY()));
		gen.writeFieldName("z");
		gen.writeRawValue(String.valueOf(value.getZ()));
		gen.writeFieldName("yaw");
		gen.writeRawValue(String.valueOf(value.getYaw()));
		gen.writeFieldName("pitch");
		gen.writeRawValue(String.valueOf(value.getPitch()));
		gen.writeFieldName("world_uuid");
		gen.writeString(value.getWorld().getUID().toString());
		gen.writeEndObject();
	}
}
