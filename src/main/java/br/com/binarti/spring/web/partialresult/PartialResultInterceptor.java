package br.com.binarti.spring.web.partialresult;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.binarti.sjog.ObjectGraph;
import br.com.binarti.sjog.ObjectGraphBuilder;

@Aspect
@Component
public class PartialResultInterceptor {
	
	private static final Logger logger = Logger.getLogger(PartialResultInterceptor.class);
	
	private static final Map<Method, ObjectGraphBuilder> templateCache = new LRUMap<>(300);
	
	@Autowired
	private PartialResultProvider partialResultProvider;
	
	@SuppressWarnings("unchecked")
	@Around("@annotation(br.com.binarti.spring.web.partialresult.annotation.PartialResult)")
	public Object intercept(ProceedingJoinPoint jp) throws Throwable {
		Object response = jp.proceed();
		MethodSignature methodSignature = (MethodSignature) jp.getSignature();
		Method method = methodSignature.getMethod();
		
		logger.debug("Returned value: " + response);
		
		if (!Response.class.isAssignableFrom(method.getReturnType())) {
			throw new PartialResultException("Invalid return type for parcial result in method " + method + ". The return type must be " + Response.class.getName());
		}
		Response<Object> partialResponse = (Response<Object>) response;
		
		ObjectGraphBuilder template = templateCache.get(method);
		if (template == null) {
			logger.debug("Creating new ObjectGraphBuilder for method " + method);
			template = partialResultProvider.createTemplate(method, partialResponse);
			templateCache.put(method, template);
		} else {
			logger.debug("Using cached template for method " + method);
		}
		
		ObjectGraph graph = template.build(partialResponse.getData());
		logger.debug("Object graph response: " + graph.getNodes());
		partialResponse.setObjectGraph(graph);
		
		return response;
	}
	
}
