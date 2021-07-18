package me.MFHKiwi.KiwiClaims.Utilities;

import java.io.IOException;

import org.bukkit.Location;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import me.MFHKiwi.KiwiClaims.KClaim;

public class KClaimSerializer extends StdSerializer<KClaim> {
	private static final long serialVersionUID = 1L;
	private final ObjectMapper mapper = new ObjectMapper();
	
	public KClaimSerializer() {
		this(null);
	}
	
	private KClaimSerializer(Class<KClaim> t) {
		super(t);
		SimpleModule module = new SimpleModule();
		module.addSerializer(Location.class, new KLocationSerializer());
		mapper.registerModule(module);
	}

	@Override
	public void serialize(KClaim value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.useDefaultPrettyPrinter();
		gen.writeStartObject();
		gen.writeFieldName("min");
		gen.writeRawValue(mapper.writeValueAsString(value.getMin()));
		gen.writeFieldName("max");
		gen.writeRawValue(mapper.writeValueAsString(value.getMax()));
		gen.writeFieldName("owner");
		gen.writeString(value.getOwnerName());
		gen.writeFieldName("trusted");
		gen.writeRawValue(mapper.writeValueAsString(value.getTrusted()));
		gen.writeFieldName("uuid");
		gen.writeRawValue(mapper.writeValueAsString(value.getUUID()));
		gen.writeEndObject();
	}
}
