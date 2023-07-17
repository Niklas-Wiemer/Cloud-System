package baunetzwerk.cloud.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class LoggerBuilder {

    public static Logger LoggerBuilder(String loggerName) {
        Logger logger = Logger.getLogger(loggerName);
        logger.setUseParentHandlers(false);
        LoggerFormatter loggerFormatter = new LoggerFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(loggerFormatter);
        logger.addHandler(consoleHandler);
        return logger;
    }

}
