package br.com.binarti.spring.web.partialresult.serializer;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Component used to detect all custom jackson json serializer
 * 
 * @author francofabio
 *
 */
@Component
@Scope(value=WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class JacksonSerializersManager {

	private static final Logger logger = getLogger(JacksonSerializersManager.class);
	
	@Autowired(required=false)
	private Collection<JsonSerializer<?>> serializers;
	
	public JacksonSerializersManager() {
	}
	
	public void registerSerializers(ObjectMapper objectMapper) {
		if (serializers == null) return;
		
		SimpleModule simpleModule = new SimpleModule();
		Iterator<JsonSerializer<?>> it = serializers.iterator(); 
		while (it.hasNext()) {
			JsonSerializer<?> serializer = it.next(); 
			simpleModule.addSerializer(serializer);
			logger.debug("registered jackson serializer {}", serializer.getClass().getName());
		}
		objectMapper.registerModule(simpleModule);
	}
	
}
