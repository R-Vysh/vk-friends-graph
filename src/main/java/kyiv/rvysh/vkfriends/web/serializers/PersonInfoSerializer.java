package kyiv.rvysh.vkfriends.web.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import kyiv.rvysh.vkfriends.domain.PersonInfo;

public class PersonInfoSerializer extends JsonSerializer<PersonInfo> {

	@Override
	public void serialize(PersonInfo value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		System.out.println("ppc");
		gen.writeFieldName("ppc");
	}

}
