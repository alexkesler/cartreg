package org.kesler.cartreg.dao;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by alex on 30.11.14.
 */
@MappedSuperclass
public abstract class AbstractEntity {
    @Id
    @GeneratedValue
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
