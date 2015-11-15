package br.com.binarti.spring.web.partialresult.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.binarti.sjog.ObjectGraphPredicate;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Inherited
public @interface Include {

	/**
	 * Name of the property could be included
	 */
	String value();
	/**
	 * Children property could be included
	 */
	String[] includes() default {};
	/**
	 * Children property could be excluded
	 */
	String[] excludes() default {};
	/**
	 * Determine if 'primitive' properties children of the this property, could be included<br>
	 * @see {@link ObjectGraphPredicate#isPrimitive(Class)}
	 */
	boolean includePrimitives() default true;
}
