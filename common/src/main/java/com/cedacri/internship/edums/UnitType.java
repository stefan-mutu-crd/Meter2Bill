package com.cedacri.internship.edums;

import lombok.Getter;

@Getter
public enum UnitType {
    HOUSE(true, false, false, false),
    BUILDING(true, true, false, false),
    APARTMENT(true, true, true, true),
    OFFICE(true, true, true, true),
    FACTORY(true, false, false, false),
    PUBLIC_AREA(false, false, false, false),
    WAREHOUSE(true, false, false, false),
    SHOP(true, true, true, false),
    OTHER(true, true, true, true);

    private final boolean streetNumber;
    private final boolean block;
    private final boolean floor;
    private final boolean apartment;

    UnitType(boolean streetNumber, boolean block, boolean floor, boolean apartment) {
        this.streetNumber = streetNumber;
        this.block = block;
        this.floor = floor;
        this.apartment = apartment;
    }
}
