package br.com.binarti.spring.web.partialresult;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperSerializeConfigurator {

	void configure(ObjectMapper objectMapper);
	
}
