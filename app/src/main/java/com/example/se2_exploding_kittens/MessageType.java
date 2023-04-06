package com.example.se2_exploding_kittens;

public enum MessageType {
    MESSAGE("M"),
    REPLY("R"),
    UNKNOWN("U"),
    ERROR("ERR");

    public final String value;

    private MessageType(String value) {
        this.value = value;
    }
}
