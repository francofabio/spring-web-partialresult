package br.com.binarti.spring.web.partialresult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public class CalendarConverter extends JsonSerializer<Calendar> {

	@Override
	public void serialize(Calendar value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		SimpleDateFormat sdf = new SimpleDateFormat(ObjectGraphJSONSerializerTest.DATE_FORMAT);
		gen.writeString(sdf.format(value.getTime()));
	}
	
	@Override
	public Class<Calendar> handledType() {
		return Calendar.class;
	}

}
