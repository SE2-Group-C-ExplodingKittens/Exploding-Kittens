package com.example.se2_exploding_kittens.Network;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.NetworkManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerManager implements MessageCallback, ClientConnectedCallback, DisconnectedCallback {

    private static PlayerManager instance = null;
    private static final int PLAYER_MANAGER_MESSAGE_ID = 400;
    private List<PlayerConnection> playerConnections;
    private int nextPlayerID;

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    private PlayerManager() {
        this.playerConnections = new ArrayList<>();
        this.nextPlayerID = 0;
    }

    public void initialize(ArrayList<ServerTCPSocket> players, NetworkManager networkManager) {
        networkManager.subscribeCallbackToMessageID(this, PLAYER_MANAGER_MESSAGE_ID);
        for (ServerTCPSocket player : players) {
            assignPlayerID(player);
        }
    }

    private void assignPlayerID(ServerTCPSocket connection) {
        int playerID = nextPlayerID++;
        PlayerConnection playerConnection = new PlayerConnection(connection, playerID);
        playerConnections.add(playerConnection);
    }

    public PlayerConnection getPlayer(int playerId) {
        for (PlayerConnection playerConnection : playerConnections) {
            if (playerConnection.getPlayerID() == playerId) {
                return playerConnection;
            }
        }
        return null; // player not found
    }

    public int getPlayerSize() {
        return playerConnections.size();
    }

    public void shuffle() {
        Collections.shuffle(playerConnections);
    }

    public int getPlayerIDByConnection(ServerTCPSocket connection) {
        for (PlayerConnection playerConnection : playerConnections) {
            if (playerConnection.getConnection() == connection) {
                return playerConnection.getPlayerID();
            }
        }
        return -1; // player not found
    }

    private void disconnectPlayer(ServerTCPSocket connection) {
        playerConnections.remove(getPlayer(getPlayerIDByConnection(connection)));
    }

    @Override
    public void clientConnected(ServerTCPSocket connection) {
        assignPlayerID(connection);
    }

    @Override
    public void connectionDisconnected(Object connection) {
        if (connection instanceof ServerTCPSocket) {
            disconnectPlayer((ServerTCPSocket) connection);
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {

    }
}