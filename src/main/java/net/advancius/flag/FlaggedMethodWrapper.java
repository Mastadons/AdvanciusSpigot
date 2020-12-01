package net.advancius.flag;

import lombok.Data;
import net.advancius.utils.Reflection;

import java.lang.reflect.Method;

@Data
public class FlaggedMethodWrapper implements Comparable<FlaggedMethodWrapper> {

    private final Class<?> clazz;
    private final Method method;
    private final FlagManager.FlaggedMethod flag;

    @Override
    public int compareTo(FlaggedMethodWrapper other) {
        return this.getPriority() - other.getPriority();
    }

    public Object invoke() {
        try {
            return Reflection.runMethod(method, clazz);
        } catch (RuntimeException e) { e.printStackTrace(); }
        return null;
    }

    public int getPriority() {
        return flag.priority();
    }

    public DefinedFlag getFlag() {
        return flag.flag();
    }
}
