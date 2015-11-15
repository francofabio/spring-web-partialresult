package br.com.binarti.spring.web.partialresult.serializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
@Scope(value=WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DefaultJacksonSerializerBuilder implements JacksonSerializerBuilder {
	
	private final JacksonSerializersManager jacksonSerializerManager;
	
	@Autowired
	public DefaultJacksonSerializerBuilder(JacksonSerializersManager jacksonSerializersManager) {
		this.jacksonSerializerManager = jacksonSerializersManager;
	}
	
	@Override
	public ObjectMapper newObjectMapper() {
		return configure(new ObjectMapper());
	}

	@Override
	public ObjectMapper configure(ObjectMapper objectMapper) {
		objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		
		jacksonSerializerManager.registerSerializers(objectMapper);
		return objectMapper;
	}

	@Override
	public ObjectMapper newIndentedObjectMapper() {
		ObjectMapper objectMapper = configure(new ObjectMapper());
		return objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

}
