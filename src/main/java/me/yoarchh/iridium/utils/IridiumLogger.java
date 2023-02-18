package me.yoarchh.iridium.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

public final class IridiumLogger
{
    private final String loggerName;

    private final Logger logger = (Logger) LogManager.getLogger("Iridium");

    public IridiumLogger(String name)
    {
        this.loggerName = name;
    }

    public void trace(String message, Object ... arguments)
    {
        logger.trace("[{}] {}", loggerName, ParameterizedMessage.format(message, arguments));
    }

    public void info(String message, Object ... arguments)
    {
        logger.info("[{}] {}", loggerName, ParameterizedMessage.format(message, arguments));
    }

    public void debug(String message, Object ... arguments)
    {
        logger.debug("[{}] {}", loggerName, ParameterizedMessage.format(message, arguments));
    }

    public void warn(String message, Object ... arguments)
    {
        logger.warn("[{}] {}", loggerName, ParameterizedMessage.format(message, arguments));
    }

    public void error(String message, Object ... arguments)
    {
        logger.error("[{}] {}", loggerName, ParameterizedMessage.format(message, arguments));
    }

    public void fatal(String message, Object ... arguments)
    {
        logger.fatal("[{}] {}", loggerName, ParameterizedMessage.format(message, arguments));
    }

    public String getName() { return this.loggerName; }
}
