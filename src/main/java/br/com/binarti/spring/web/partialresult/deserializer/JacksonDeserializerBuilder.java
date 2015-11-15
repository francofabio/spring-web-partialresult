package br.com.binarti.spring.web.partialresult.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interface that defines method for create and configure ObjectMapper instances for deserialization
 * 
 * @author francofabio
 *
 */
public interface JacksonDeserializerBuilder {
	
	ObjectMapper newObjectMapper();
	
	ObjectMapper configure(ObjectMapper objectMapper);
	
}
