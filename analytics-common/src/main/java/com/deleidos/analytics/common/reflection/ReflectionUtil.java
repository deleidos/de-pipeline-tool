package com.deleidos.analytics.common.reflection;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Reusable reflection utility methods.
 * 
 * @author vernona
 */
public class ReflectionUtil {

	/**
	 * Construct an object by class name with constructor parameters as an array.
	 * 
	 * @param className
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Object constructObjectWithParameters(String className, Object[] params) throws Exception {
		Class<?>[] classParams = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			classParams[i] = params[i].getClass();
		}

		Class<?> c = Class.forName(className);
		Constructor<?> constructor = c.getConstructor(classParams);
		return constructor.newInstance(params);
	}

	/**
	 * Construct an object by class name with constructor parameters as a list.
	 * 
	 * @param className
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Object constructObjectWithParameters(String className, List<Object> params) throws Exception {
		return constructObjectWithParameters(className, params.toArray(new Object[params.size()]));
	}
}
