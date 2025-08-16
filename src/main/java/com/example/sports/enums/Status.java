package com.example.sports.enums;

public enum Status {

    LIVE("live"), NOT_LIVE("not live");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
