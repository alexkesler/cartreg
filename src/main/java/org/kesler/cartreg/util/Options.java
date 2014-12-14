package org.kesler.cartreg.util;

import java.util.Properties;

/**
 * Created by alex on 14.12.14.
 */
public class Options {
    private static Properties options = new Properties();

    public static void addOption(String key,String value) {
        options.setProperty(key, value);
    }

    public static String getOption(String key) {
        return options.getProperty(key);
    }

    public static Properties getOptions() {return options;}
}

