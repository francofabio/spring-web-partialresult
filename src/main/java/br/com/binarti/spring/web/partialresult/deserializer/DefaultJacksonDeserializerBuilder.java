package br.com.binarti.spring.web.partialresult.deserializer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope(value=WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DefaultJacksonDeserializerBuilder implements JacksonDeserializerBuilder {
	
	private final JacksonDeserializersManager jacksonSerializerManager;
	
	@Autowired
	public DefaultJacksonDeserializerBuilder(JacksonDeserializersManager jacksonDeserializersManager) {
		this.jacksonSerializerManager = jacksonDeserializersManager;
	}
	
	@Override
	public ObjectMapper newObjectMapper() {
		return configure(new ObjectMapper());
	}

	@Override
	public ObjectMapper configure(ObjectMapper objectMapper) {	    
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		objectMapper.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
		objectMapper.disable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		jacksonSerializerManager.registerDeserializers(objectMapper);
		return objectMapper;
	}

}
