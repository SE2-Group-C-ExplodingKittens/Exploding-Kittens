package com.example.se2_exploding_kittens.Network;

public enum MessageType {
    MESSAGE("M"),
    REPLY("R"),
    UNKNOWN("U"),
    ERROR("ERR"),

    CHECKED_DETAILS("D");

    public final String value;

    MessageType(String value) {
        this.value = value;
    }
}
