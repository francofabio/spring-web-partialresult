package br.com.binarti.spring.web.partialresult.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interface that defines method for create and configure ObjectMapper instances for serialization
 * 
 * @author francofabio
 *
 */
public interface JacksonSerializerBuilder {
	
	ObjectMapper newObjectMapper();
	
	ObjectMapper configure(ObjectMapper objectMapper);
	
	ObjectMapper newIndentedObjectMapper();
	
}
