package org.kesler.cartreg.domain;

import java.util.Date;
import java.util.UUID;

/**
 * Created by alex on 25.11.14.
 */
public class CartSetChange {

    private String uuid = UUID.randomUUID().toString();
    private CartType cartType;
    private Place fromPlace;
    private Place toPlace;
    private CartStatus fromStatus;
    private CartStatus toStatus;
    private Integer quantity;
    private Date changeDate;
}
