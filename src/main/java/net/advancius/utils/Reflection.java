package net.advancius.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static boolean assignField(Object object, Field field, Object columnValue) {
        try {
            field.setAccessible(true);
            field.set(object, columnValue);
            field.setAccessible(false);
            return true;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return false;
        }
    }

    public static Object accessField(Object object, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(object);
            field.setAccessible(false);
            return value;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            field.setAccessible(false);
            return false;
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameters) {
        try {
            return clazz.getMethod(name, parameters);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    public static Object runMethod(Method method, Object object, Object... parameters) {
        try {
            method.setAccessible(true);
            Object value = method.invoke(object, parameters);
            method.setAccessible(false);
            return value;
        } catch (Exception e) {
            method.setAccessible(true);
            throw new RuntimeException(e);
        }
    }

    public static <T> T runEmptyConstructor(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    public static <T> T runConstructor(Constructor<T> constructor, Object... parameters) {
        if (constructor == null) return null;
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            return null;
        }
    }

    public static Class<?>[] getClasses(Object[] objects) {
        Class<?>[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) classes[i] = objects[i].getClass();

        return classes;
    }

}
