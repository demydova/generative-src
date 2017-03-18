//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
//Author: 		Ganna Demydova
//Description: 	Instancing of the variable of the class without constructor
//Version		00.04 12.03.2017
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////

package framework;

import sun.reflect.ReflectionFactory;
import java.lang.reflect.Constructor;

public class SilentObjectCreator {
	
	//wrapper function for subsequent method
	public static <T> T create(Class<T> clazz) {
		return create(clazz, Object.class);
	}

	// inserting of new constructor from parent class with reflection factory
	public static <T> T create(Class<T> clazz, Class<? super T> parent) {
		try {
			ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
			Constructor objDef = parent.getDeclaredConstructor();
			Constructor intConstr = rf.newConstructorForSerialization(clazz, objDef);
			return clazz.cast(intConstr.newInstance());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("Exception: cannot create object", e);
		}
	}
}