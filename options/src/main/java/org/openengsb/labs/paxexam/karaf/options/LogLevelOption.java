package org.openengsb.labs.paxexam.karaf.options;

import org.ops4j.pax.exam.Option;

/**
 * While the log-level could also be configured using the config options in the configuration files we also provide a
 * more easy option here.
 */
public class LogLevelOption implements Option {

    public static enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private LogLevel logLevel;

    public LogLevelOption() {
    }

    public LogLevelOption(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public LogLevelOption logLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

}
