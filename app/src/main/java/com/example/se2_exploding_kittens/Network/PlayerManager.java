package com.example.se2_exploding_kittens.Network;

import android.util.Log;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.game_logic.Player;

import java.util.ArrayList;
import java.util.Collections;

public class PlayerManager implements MessageCallback, ClientConnectedCallback, DisconnectedCallback {


    //payload has the pattern TYPE:DATA

    private static String DEBUG_TAG = "PlayerManager";
    private Player playerClient;
    private static PlayerManager instance = null;
    private static final int PLAYER_MANAGER_MESSAGE_ID = 400;
    private final ArrayList<PlayerConnection> playerConnections;
    private int nextPlayerID;
    private NetworkManager networkManager;

    private static final int PLAYER_MANAGER_ID_ASSIGNED = 1;
    private static final int PLAYER_MANAGER_ID_PLAYER_DISCONNECT = 99;


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

    //Initalize as host, as the host assigns player numbers
    public void initializeAsHost(ArrayList<ServerTCPSocket> connections, NetworkManager networkManager) {
        if(networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER){
            // selfassign
            assignPlayerID(null);
            this.networkManager = networkManager;
            this.networkManager.subscribeCallbackToMessageID(this, PLAYER_MANAGER_MESSAGE_ID);
            for (ServerTCPSocket player : connections) {
                assignPlayerID(player);
            }
        }
    }

    //Initalize as client, to listen for player numbers
    public void initializeAsClient(Player player, NetworkManager networkManager){
        if(networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT){
            playerClient = player;
            this.networkManager = networkManager;
            this.networkManager.subscribeCallbackToMessageID(this, PLAYER_MANAGER_MESSAGE_ID);
        }
    }

    private int parseTypeFromPayload(String input) {
        String[] splitInput = input.split(":");
        if (splitInput.length > 0) {
            try {
                return Integer.parseInt(splitInput[0]);
            } catch (NumberFormatException e) {
                Log.e(DEBUG_TAG, "Could not parse");
            }
        }
        return -1;  // -1 means invalid
    }

    private String parseDataFromPayload(String input) {
        String[] splitInput = input.split(":");
        if (splitInput.length > 1) {
            return splitInput[1];
        }
        return null;  // null means invalid
    }

    private Message createMessage(int type, String data){
        return new Message(MessageType.MESSAGE,PLAYER_MANAGER_MESSAGE_ID,type+":"+data);
    }

    private void assignPlayerID(ServerTCPSocket connection) {
        int playerID = nextPlayerID++;
        PlayerConnection playerConnection = new PlayerConnection(connection, playerID);
        playerConnections.add(playerConnection);
        if(networkManager != null){
            try {
                networkManager.sendMessageFromTheSever(createMessage(PLAYER_MANAGER_ID_ASSIGNED,playerID+""),connection);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public PlayerConnection getPlayer(int playerId) {
        for (PlayerConnection playerConnection : playerConnections) {
            if (playerConnection.getPlayerID() == playerId) {
                return playerConnection;
            }
        }
        return null; // player not found
    }

    public ArrayList<PlayerConnection> getPlayers() {
        return playerConnections;
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

    public void removePlayer(PlayerConnection player) {
        if(playerConnections.contains(player)){
            if(networkManager != null){
                if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER){
                    try {
                        networkManager.sendMessageFromTheSever(createMessage(PLAYER_MANAGER_ID_PLAYER_DISCONNECT,player.getPlayerID()+""), player.getConnection());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void playerDisconnected(ServerTCPSocket connection) {
        playerConnections.remove(getPlayer(getPlayerIDByConnection(connection)));
    }

    @Override
    public void clientConnected(ServerTCPSocket connection) {
        assignPlayerID(connection);
    }

    @Override
    public void connectionDisconnected(Object connection) {
        if (connection instanceof ServerTCPSocket) {
            playerDisconnected((ServerTCPSocket) connection);
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {
        if(networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT){
            int playerID = -1;
            if(text !=  null){
                switch (parseTypeFromPayload(Message.parseAndExtractPayload(text))){
                    case PLAYER_MANAGER_ID_ASSIGNED:
                        playerID = Integer.parseInt(parseDataFromPayload(Message.parseAndExtractPayload(text)));
                        if(playerID != -1){
                            playerClient.setPlayerId(playerID);
                        }
                        break;
                    case PLAYER_MANAGER_ID_PLAYER_DISCONNECT:
                        playerID = Integer.parseInt(parseDataFromPayload(Message.parseAndExtractPayload(text)));
                        if(playerID != playerClient.getPlayerId()){
                            networkManager.terminateConnection();
                        }
                        break;
                }
            }
        }
    }
}