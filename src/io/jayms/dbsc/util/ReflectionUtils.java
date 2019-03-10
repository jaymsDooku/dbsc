package io.jayms.dbsc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReflectionUtils {

	public static Class<?> getDeclaredClass(Class<?> enclosingClazz, String name) {
		return Arrays.stream(enclosingClazz.getDeclaredClasses()).filter(c -> c.getName().equals(name)).findFirst().orElse(null);
	}
	
	public static Object getField(Class<?> enclosingClazz, Object instance, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = enclosingClazz.getDeclaredField(name);
		field.setAccessible(true);
		return field.get(instance);
	}
	
	public static void setField(Class<?> enclosingClazz, Object instance, String name, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = enclosingClazz.getDeclaredField(name);
		
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
	    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		
		field.set(instance, value);
	}
	
}
