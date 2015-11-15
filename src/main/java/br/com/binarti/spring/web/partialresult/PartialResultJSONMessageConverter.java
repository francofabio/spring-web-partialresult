package br.com.binarti.spring.web.partialresult;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.binarti.spring.web.partialresult.deserializer.JacksonDeserializerBuilder;
import br.com.binarti.spring.web.partialresult.serializer.JacksonSerializerBuilder;

public class PartialResultJSONMessageConverter extends MappingJackson2HttpMessageConverter {

	private static final Logger logger = Logger.getLogger(PartialResultJSONMessageConverter.class);
	
	@Autowired
	private JacksonSerializerBuilder jacksonSerializerBuilder;
	@Autowired
	private JacksonDeserializerBuilder jacksonDeserializerBuilder;
	
	private boolean indented = false;
	
	public PartialResultJSONMessageConverter() {
		setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json", DEFAULT_CHARSET), new MediaType("application", "*+json", DEFAULT_CHARSET)));
	}
	
	public boolean isIndented() {
		return indented;
	}
	
	public void setIndented(boolean indented) {
		this.indented = indented;
	}
	
	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return canWrite(mediaType);
	}
	
	@Override
	protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		writeInternal(object, outputMessage);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		ObjectMapper objectMapper = (indented) ? jacksonSerializerBuilder.newIndentedObjectMapper() : jacksonSerializerBuilder.newObjectMapper();
		JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
		JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
		Object value = object;
		if (value instanceof Response) {
			logger.debug("Partial result message converter ");
			try {
				Response<Object> response = (Response<Object>) value;
				if (response.getObjectGraph() != null) {
					logger.debug("Start ObjectGraph serialization");
					ObjectGraphJSONSerializer serializer = new ObjectGraphJSONSerializer(response.getObjectGraph());
					logger.debug("End ObjectGraph serialization");
					value = serializer.serialize(objectMapper);
				} else {
					logger.warn("ObjectGraph is null. Please check if aspectj are enable.");
					value = response.getData();
				}
			} catch (Exception e) {
				throw new HttpMessageConversionException("Unable to serialize data", e);
			}
		}
		try {
			objectMapper.writeValue(jsonGenerator, value);
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
		}
	}
	
	private Object readObject(Type type, HttpInputMessage inputMessage, Class<?> contextClass)  throws IOException, HttpMessageNotReadableException {
		JavaType javaType = getJavaType(type, contextClass);
		try {
			ObjectMapper objectMapper = jacksonDeserializerBuilder.newObjectMapper();
			return objectMapper.readValue(inputMessage.getBody(), javaType);
		} catch (IOException ex) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}
	
	@Override
	protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return readObject(clazz, inputMessage, null);
	}
	
	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return readObject(type, inputMessage, contextClass);
	}
	
}
