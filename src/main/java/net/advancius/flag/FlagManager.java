package net.advancius.flag;

import net.advancius.AdvanciusLogger;
import org.reflections.Reflections;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FlagManager {

    public static <T> List<FlaggedMethodWrapper> getFlaggedMethods(Class<T> clazz) {
        List<FlaggedMethodWrapper> methods = new ArrayList<>();
        if (!clazz.isAnnotationPresent(FlaggedClass.class)) return methods;
        for (Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) continue;
            if (method.getParameterCount() != 0) continue;
            if (!method.isAnnotationPresent(FlaggedMethod.class)) continue;
            FlaggedMethod[] flags = method.getAnnotationsByType(FlaggedMethod.class);
            for (FlaggedMethod flag : flags) methods.add(new FlaggedMethodWrapper(clazz, method, flag));
        }
        return methods;
    }

    public static <T> List<FlaggedMethodWrapper> getFlaggedMethods(Class<T> clazz, DefinedFlag flag) {
        List<FlaggedMethodWrapper> methods = getFlaggedMethods(clazz);
        methods.removeIf(method -> method.getFlag() != flag);
        return methods;
    }

    public static List<FlaggedMethodWrapper> getFlaggedMethods(String packageName, DefinedFlag flag) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> flaggedClasses = reflections.getTypesAnnotatedWith(FlaggedClass.class);
        List<FlaggedMethodWrapper> methods = new ArrayList<>();
        for (Class<?> flaggedClass : flaggedClasses) methods.addAll(getFlaggedMethods(flaggedClass, flag));
        Collections.sort(methods);
        return methods;
    }

    public static void runFlaggedMethods(String packageName, DefinedFlag flag) {
        AdvanciusLogger.info("Running all methods flagged with: " + flag.name());
        for (FlaggedMethodWrapper method : getFlaggedMethods(packageName, flag)) method.invoke();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface FlaggedClass {}

    @Repeatable(value = FlagRepeater.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface FlaggedMethod {

        DefinedFlag flag();
        int priority() default 0;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface FlagRepeater {
        FlaggedMethod[] value();
    }

}
