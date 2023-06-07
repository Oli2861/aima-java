package main.java.com.oli.utility;

public enum LOG_LEVEL {
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4);
    final int level;

    LOG_LEVEL(int level) {
        this.level = level;
    }
}
