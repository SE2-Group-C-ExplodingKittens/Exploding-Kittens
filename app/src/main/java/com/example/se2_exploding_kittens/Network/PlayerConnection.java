package com.example.se2_exploding_kittens.Network;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.game_logic.Player;

public class PlayerConnection {
    private ServerTCPSocket connection;
    private Player player;

    public PlayerConnection(ServerTCPSocket connection, int playerID) {
        this.connection = connection;
        this.player = new Player(playerID);
    }

    public ServerTCPSocket getConnection() {
        return connection;
    }

    public int getPlayerID() {
        return player.getPlayerId();
    }

    public Player getPlayer() {
        return player;
    }

    public int numberOfTurnsLeft(){
        return player.getPlayerTurns();
    }
}