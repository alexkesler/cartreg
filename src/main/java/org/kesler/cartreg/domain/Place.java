package org.kesler.cartreg.domain;

import org.kesler.cartreg.dao.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.UUID;

/**
 * Класс для размещения
 */
@Entity
public class Place extends AbstractEntity{
    @Column(name="UUID",length=36)
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "Name")
    private String name = "";

    @Enumerated(EnumType.STRING)
    private Type type = Type.BRANCH;

    public String getUuid() {return uuid;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

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

    public enum Type {
        DIRECT("Дирекция"),
        STORAGE("Склад"),
        BRANCH("Филиал");

        private String desc;

        Type(String desc) {
            this.desc = desc;
        }

        public String getDesc() { return  desc; }
    }
}
