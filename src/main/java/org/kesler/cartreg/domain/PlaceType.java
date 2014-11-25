package org.kesler.cartreg.domain;

/**
 * Типы размещений
 */
public enum PlaceType {
    DIRECT("Дирекция"),
    STORAGE("Склад"),
    BRANCH("Филиал");

    private String desc;

    PlaceType(String desc) {
        this.desc = desc;
    }

    public String getDesc() { return  desc; }
}
