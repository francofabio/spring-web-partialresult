package br.com.binarti.spring.web.partialresult.deserializer;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Component used to detect all custom jackson json deserializer
 * 
 * @author francofabio
 *
 */
@Component
@Scope(value=WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class JacksonDeserializersManager {

	private static final Logger logger = getLogger(JacksonDeserializersManager.class);

	@Autowired(required = false)
	private Collection<JsonDeserializer<?>> deserializers;
	
	public JacksonDeserializersManager() {
	}

	@SuppressWarnings({ "unchecked" })
	public void registerDeserializers(ObjectMapper objectMapper) {
		if (deserializers == null || deserializers.isEmpty()) return;

		SimpleModule simpleModule = new SimpleModule();
		Iterator<JsonDeserializer<?>> it = deserializers.iterator();
		while (it.hasNext()) {
			JsonDeserializer<?> deserializer = it.next();
			simpleModule.addDeserializer((Class<Object>) deserializer.handledType(), deserializer);
			logger.debug("registered jackson deserializer {}", deserializer.getClass().getName());
		}
		objectMapper.registerModule(simpleModule);
	}

}
