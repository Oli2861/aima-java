package main.java.com.oli.utility;

import java.util.Date;

public class Logger {
    private final String name;
    private final static LogLevel logLevel = LogLevel.DEBUG;
    private final String debugPrefix = "[DEBUG] ";
    private final String infoPrefix = "[INFO] ";
    private final String warnPrefix = "[WARN] ";
    private final String errorPrefix = "[ERROR] ";

    public Logger(String name) {
        this.name = name;
    }

    private void log(String levelPrefix, String message) {
        System.out.println("[" + new Date() + "] " + levelPrefix + name + ": " + message);
    }

    public void info(String message) {
        if (logLevel.level >= LogLevel.INFO.level)
            log(infoPrefix, message);
    }

    public void debug(String message) {
        if (logLevel.level >= LogLevel.DEBUG.level)
            log(debugPrefix, message);
    }

    public void warn(String message) {
        if (logLevel.level >= LogLevel.WARN.level)
            log(warnPrefix, message);
    }

    public void error(String message) {
        if (logLevel.level >= LogLevel.ERROR.level)
            log(errorPrefix, message);
    }

}
