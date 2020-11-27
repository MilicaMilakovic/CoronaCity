package net.etfbl.java;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

    private static Logger logger;

    private MyLogger() {}

    public static void setup() throws Exception
    {
        logger=Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        FileHandler handler=new FileHandler("log.txt",true);
        SimpleFormatter simpleFormatter=new SimpleFormatter();
        logger.setUseParentHandlers(false);
        handler.setFormatter(simpleFormatter);
        logger.setLevel(Level.INFO);
        logger.addHandler(handler);
    }

    public static void log(Level level, String poruka, Exception e)
    {
        logger.log(level,poruka,e);
    }
}
