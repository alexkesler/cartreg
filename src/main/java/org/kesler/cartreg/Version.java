package org.kesler.cartreg;

/**
 * Класс для хранения версии приложения
 */
public abstract class Version {

    private static String version = "1.0.0.0";
    private static String releaseDate = "12.12.2014";

    public static String getVersion() {
        return version;
    }

    public static String getReleaseDate() {
        return releaseDate;
    }
}
