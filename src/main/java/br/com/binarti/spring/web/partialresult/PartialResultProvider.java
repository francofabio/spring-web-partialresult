package br.com.binarti.spring.web.partialresult;

import java.lang.reflect.Method;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import br.com.binarti.sjog.ObjectGraphBuilder;
import br.com.binarti.spring.web.partialresult.annotation.Include;
import br.com.binarti.spring.web.partialresult.annotation.PartialResult;

@Component
public class PartialResultProvider {
	
	private static final Logger logger = Logger.getLogger(PartialResultProvider.class);
	
	public PartialResultProvider() {
	}
	
	private void excludesPropertiesFromTemplate(ObjectGraphBuilder template, String[] properties, String root) {
		if (properties != null && properties.length > 0) {
			for (String prop : properties) {
				StringJoiner path = new StringJoiner(".");
				if (root != null) {
					path.add(root);
				}
				String propertyName = path.add(prop).toString(); 
				template.exclude(propertyName);
				logger.debug("Property " + propertyName + " exclued");
			}
		}
	}
	
	public PartialResult extractPartialResult(Method method) {
		PartialResult partialResult = method.getAnnotation(PartialResult.class);
		//check for override methods
		if (partialResult == null) {
			Class<?> parentClass = method.getDeclaringClass().getSuperclass();
			if (parentClass == Object.class) {
				return null;
			}
			try {
				Method parentMethod = parentClass.getMethod(method.getName(), method.getParameterTypes());
				return extractPartialResult(parentMethod);
			} catch (NoSuchMethodException e) {
				return null;
			} catch (SecurityException e) {
				throw new PartialResultException("Error while getting parent method " + method.getName() + " in parent class " + method.getDeclaringClass(), e);
			}
		}
		return partialResult;
	}
	
	public ObjectGraphBuilder createTemplate(Method method) {
		return createTemplate(method, Response.empty());
	}
	
	public ObjectGraphBuilder createTemplate(Method method, Response<Object> response) {
		PartialResult partialResult = extractPartialResult(method);
		if (partialResult == null) {
			throw new PartialResultException("Method " + method + " intercepted, but this method is not annoted by " + PartialResult.class.getName());
		}
		
		ObjectGraphBuilder template = new ObjectGraphBuilder();
		if (!partialResult.includePrimitives()) {
			template.autoIncludePrimitivesFromRoot(false); 
			logger.debug("Not include primitives properties for root node.");
		}
		if (partialResult.includes() != null && partialResult.includes().length > 0) {
			for (Include propToInclude : partialResult.includes()) {
				String name = propToInclude.value();
				if (!propToInclude.includePrimitives()) {
					template.autoIncludePrimitives(name, false);
				}
				template.include(name);
				logger.debug("Property " + name + " included");
				if (propToInclude.includes() != null && propToInclude.includes().length > 0) {
					for (String innerProperty : propToInclude.includes()) {
						StringJoiner path = new StringJoiner(".");
						String nestedPropertyName = path.add(name).add(innerProperty).toString(); 
						template.include(nestedPropertyName);
						logger.debug("Nested property " + nestedPropertyName + " included");
					}
				}
				excludesPropertiesFromTemplate(template, propToInclude.excludes(), name);
			}
		}
		excludesPropertiesFromTemplate(template, partialResult.excludes(), null);
		
		return template;
	}
	
}
