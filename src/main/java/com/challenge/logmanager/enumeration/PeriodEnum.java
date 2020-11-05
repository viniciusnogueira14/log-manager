package com.challenge.logmanager.enumeration;


public enum PeriodEnum {

    BEFORE("BEFORE"),
    AFTER("AFTER"),
    EQUALS("EQUALS");

    private final String acronym;

    public String getArconym() {
        return acronym;
    }

    PeriodEnum(String acronym) {
        this.acronym = acronym;
    }

}
