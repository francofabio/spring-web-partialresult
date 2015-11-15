package br.com.binarti.spring.web.partialresult;

import static br.com.binarti.sjog.ObjectGraphHelper.isCollection;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
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
	
	@SuppressWarnings("rawtypes")
	private Object firstValueInCollection(Object collection) {
		if (collection == null) {
			return null;
		}
		if (collection instanceof List) {
			return (!((List) collection).isEmpty()) ? ((List) collection).get(0) : null;
		} else if (collection.getClass().isArray()) {
			return (((Object[]) collection).length > 0) ? ((Object[]) collection)[0] : null;
		} else {
			throw new IllegalArgumentException("Unsupported collection type " + collection.getClass());
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Class<?> resolveTypeVariable(TypeVariable typeVariable) {
		return (Class<?>) typeVariable.getBounds()[0];
	}
	
	@SuppressWarnings("rawtypes")
	private Class<?> getResponseType(Method method) {
		Type genericReturnType = method.getGenericReturnType();
		if (genericReturnType instanceof ParameterizedType) {
			ParameterizedType pType = ((ParameterizedType) genericReturnType);
			//Nested parameter
			if (pType.getActualTypeArguments()[0] instanceof ParameterizedType) {
				ParameterizedType innerPType = (ParameterizedType) pType.getActualTypeArguments()[0];
				if (Collection.class.isAssignableFrom((Class<?>) innerPType.getRawType())) {
					if (innerPType.getActualTypeArguments()[0] instanceof TypeVariable) {
						return resolveTypeVariable((TypeVariable) innerPType.getActualTypeArguments()[0]);
					}
					return (Class<?>) innerPType.getActualTypeArguments()[0];
				}
				return (Class<?>) innerPType.getRawType();
			} else {
				if (pType.getActualTypeArguments()[0] instanceof TypeVariable) {
					return resolveTypeVariable((TypeVariable) pType.getActualTypeArguments()[0]);
				}
				return (Class<?>) pType.getActualTypeArguments()[0];
			}
		}
		return method.getReturnType();
	}
	
	private Class<?> inferCollectionType(Object collection) {
		Object firstVal = firstValueInCollection(collection);
		if (firstVal != null) {
			return firstVal.getClass();
		}
		return null;
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
		Class<?> type = partialResult.type();
		Object value = response.getData();
		if (type == void.class) {
			type = getResponseType(method);
			if (value != null && type.equals(Object.class)) {
				type = value.getClass();
			} else if (value == null && type.equals(Object.class)) {
				throw new PartialResultException("Impossible to determine the type of the partial result when target is Object and returned value is null");
			}
			logger.debug("Resolved type " + type);
			//When return is a collection, the type should be the component type
			if (isCollection(type)) {
				logger.debug("Detect collection type");
				Class<?> genericReturnType = getResponseType(method);
				//When generic type is Object.class, then infer type by collection content
				if (genericReturnType == Object.class || isCollection(type)) {
					if (value != null) {
						Class<?> inferred = inferCollectionType(value);
						if (inferred != null) {
							type = inferred;
							logger.debug("Collection type detected by infer " + type);
						}
					}
				} else {
					type = genericReturnType;
					logger.debug("Collection type detected using generics " + type);
				}
			}
		}
		ObjectGraphBuilder template = new ObjectGraphBuilder(type);
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
