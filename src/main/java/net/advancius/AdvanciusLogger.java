package net.advancius;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvanciusLogger {

    private static Logger instance;

    static {
        AdvanciusLogger.instance = AdvanciusSpigot.getInstance().getLogger();
    }

    public static void info(String message) {
        instance.log(Level.INFO, message);
    }

    public static void warn(String message) {
        instance.log(Level.WARNING, message);
    }

    public static void log(Level level, String message) {
        instance.log(level, message);
    }

    public static void log(Level level, String message, Object... parameters) {
        instance.log(level, String.format(message, parameters));
    }

    public static void error(String message, Exception exception) {
        instance.severe(message);
        if (AdvanciusConfiguration.getInstance().isDebugExceptions()) exception.printStackTrace();
    }
}
