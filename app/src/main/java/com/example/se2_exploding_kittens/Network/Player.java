package com.example.se2_exploding_kittens.Network;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;

public class Player {
    private ServerTCPSocket connection;
    private int playerID;

    public Player(ServerTCPSocket connection, int playerID) {
        this.connection = connection;
        this.playerID = playerID;
    }

    public ServerTCPSocket getConnection() {
        return connection;
    }

    public int getPlayerID() {
        return playerID;
    }
}