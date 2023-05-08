package com.example.se2_exploding_kittens.Network.LobbyLogic;

import java.io.Serializable;

public class Lobby implements Serializable {
    private String name;
    private String address;
    private int port;

    public Lobby(String name, String address, int port){
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
