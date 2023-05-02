package com.example.se2_exploding_kittens.Network;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.NetworkManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerManager implements MessageCallback {
    private static final int PLAYER_MANAGER_MESSAGE_ID = 400;
    private List<Player> players;
    private int nextPlayerID;

    public PlayerManager(NetworkManager networkManager) {
        networkManager.subscribeCallbackToMessageID(this, PLAYER_MANAGER_MESSAGE_ID);
        this.players = new ArrayList<>();
        this.nextPlayerID = 0;
    }

    public void assignPlayerID(ServerTCPSocket connection) {
        int playerID = nextPlayerID++;
        Player player = new Player(connection, playerID);
        players.add(player);
    }

    public Player getPlayer(int playerId) {
        for (Player player : players) {
            if (player.getPlayerID() == playerId) {
                return player;
            }
        }
        return null; // player not found
    }

    public int getPlayerSize() {
        return players.size();
    }

    public void shuffle() {
        Collections.shuffle(players);
    }

    public int getPlayerIDByConnection(ServerTCPSocket connection) {
        for (Player player : players) {
            if (player.getConnection() == connection) {
                return player.getPlayerID();
            }
        }
        return -1; // player not found
    }

    @Override
    public void responseReceived(String text, Object sender) {
        if (sender instanceof ServerTCPSocket) {
            assignPlayerID((ServerTCPSocket) sender);
        }
    }
}