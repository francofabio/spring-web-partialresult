package br.com.binarti.spring.web.partialresult.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.binarti.sjog.ObjectGraphPredicate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface PartialResult {
	/**
	 * The of the root element
	 */
	Class<?> type() default void.class;
	/**
	 * Determine if 'primitive' properties children of the this property, could be included<br>
	 * @see {@link ObjectGraphPredicate#isPrimitive(Class)}
	 */
	boolean includePrimitives() default true;
	/**
	 * Children property could be included
	 */
	Include[] includes() default {};
	/**
	 * Children property could be excluded
	 */
	String[] excludes() default {};
}
