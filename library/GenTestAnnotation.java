//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
//Author: 		Ganna Demydova
//Description: 	Annotation interface definition, which should be imported 
//				into testing package and used for assigning of the 
//				properties/instructions for the testing engine
//Version		00.04 12.02.2017
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////

package library;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//applicable for methods and should be available in Runtime for parsing

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)

// interface definition for annotation: @GenTestAnnotation
public @interface GenTestAnnotation {
	String parameters() default "";
	String instruction() default "";
}
