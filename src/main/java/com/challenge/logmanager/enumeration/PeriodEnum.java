package com.challenge.logmanager.enumeration;


public enum PeriodEnum {

    BEFORE("BF"),
    AFTER("AF"),
    EQUALS("EQ");

    private final String acronym;

    public String getArconym() {
        return acronym;
    }

    PeriodEnum(String acronym) {
        this.acronym = acronym;
    }

}
