package framework;

import sun.reflect.ReflectionFactory;
import java.lang.reflect.Constructor;

public class SilentObjectCreator {
  public static <T> T create(Class<T> clazz) {
    return create(clazz, Object.class);
  }

  public static <T> T create(Class<T> clazz,
                             Class<? super T> parent) {
    try {
      ReflectionFactory rf =
          ReflectionFactory.getReflectionFactory();
      Constructor objDef = parent.getDeclaredConstructor();
      Constructor intConstr = rf.newConstructorForSerialization(
          clazz, objDef
      );
      return clazz.cast(intConstr.newInstance());
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("Cannot create object", e);
    }
  }
}