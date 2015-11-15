package br.com.binarti.spring.web.partialresult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public class DateConverter extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		SimpleDateFormat sdf = new SimpleDateFormat(ObjectGraphJSONSerializerTest.DATE_FORMAT);
		gen.writeString(sdf.format(value));
	}
	
	@Override
	public Class<Date> handledType() {
		return Date.class;
	}

}
