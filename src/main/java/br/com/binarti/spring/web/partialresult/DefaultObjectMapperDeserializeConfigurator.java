package br.com.binarti.spring.web.partialresult;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultObjectMapperDeserializeConfigurator implements ObjectMapperDeserializeConfigurator {

	@Override
	public void configure(ObjectMapper objectMapper) {
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		objectMapper.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
		objectMapper.disable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

}
