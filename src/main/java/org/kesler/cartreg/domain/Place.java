package org.kesler.cartreg.domain;

import java.util.UUID;

/**
 * Класс для размещения
 */
public class Place {
    private String uuid = UUID.randomUUID().toString();
    private String name = "";
    private PlaceType type = PlaceType.BRANCH;

    public String getUuid() {return uuid;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PlaceType getType() { return type; }
    public void setType(PlaceType type) { this.type = type; }

    public String getCommonName() {
        return type.getDesc() + " " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (!uuid.equals(place.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
