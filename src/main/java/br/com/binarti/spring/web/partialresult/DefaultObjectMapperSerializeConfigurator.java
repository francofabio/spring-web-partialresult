package br.com.binarti.spring.web.partialresult;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DefaultObjectMapperSerializeConfigurator implements ObjectMapperSerializeConfigurator {

	@Override
	public void configure(ObjectMapper objectMapper) {
		objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}

}
