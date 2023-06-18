package com.example.se2_exploding_kittens.Network;

import android.util.Log;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.game_logic.Player;

import java.util.ArrayList;
import java.util.Collections;

public class PlayerManager implements MessageCallback, ClientConnectedCallback, DisconnectedCallback {


    //payload has the pattern TYPE:DATA

    private static PlayerManager instance = null;
    private ArrayList<PlayerConnection> playerConnections;
    private int nextPlayerID;
    private NetworkManager networkManager;

    public static final int PLAYER_MANAGER_MESSAGE_ID = 400;
    public static final int LOCAL_PLAYER_MANAGER_ID_ASSIGNED = 1;
    public static final int LOCAL_PLAYER_MANAGER_ID_PLAYER_DISCONNECT = 99;
    public static final int LOCAL_PLAYER_MANAGER_MESSAGE_PLAYER_IDS_ID = 705;

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
        if (NetworkManager.isServer(networkManager)) {
            nextPlayerID = 0;
            this.playerConnections = new ArrayList<>();
            // selfassign
            assignPlayerID(null);
            this.networkManager = networkManager;
            this.networkManager.subscribeCallbackToMessageID(this, PLAYER_MANAGER_MESSAGE_ID);
            for (ServerTCPSocket player : connections) {
                assignPlayerID(player);
            }
            this.networkManager.subscribeToDisconnectedCallback(this);
        }
    }

    public void reset() {
        if (NetworkManager.isNotIdle(networkManager)) {
            this.networkManager.unsubscribeCallbackFromMessageID(this, PLAYER_MANAGER_MESSAGE_ID);
            this.networkManager.unsubscribeToDisconnectedCallback(this);
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                this.networkManager.unsubscribeToClientConnectedCallback(this);

            }
        }
        nextPlayerID = 0;
        this.playerConnections = new ArrayList<>();
    }


    public static int parseTypeFromPayload(String input) {
        String[] splitInput = input.split(":");
        if (splitInput.length > 0) {
            try {
                return Integer.parseInt(splitInput[0]);
            } catch (NumberFormatException e) {
                String DEBUG_TAG = "PlayerManager";
                Log.e(DEBUG_TAG, "Could not parse");
            }
        }
        return -1;  // -1 means invalid
    }

    public static String parseDataFromPayload(String input) {
        String[] splitInput = input.split(":");
        if (splitInput.length > 1) {
            return splitInput[1];
        }
        return null;  // null means invalid
    }

    private Message createMessage(int type, String data) {
        return new Message(MessageType.MESSAGE, PLAYER_MANAGER_MESSAGE_ID, type + ":" + data);
    }

    private void assignPlayerID(ServerTCPSocket connection) {
        int playerID = nextPlayerID++;
        PlayerConnection playerConnection = new PlayerConnection(connection, playerID);
        playerConnections.add(playerConnection);
        if (networkManager != null) {
            try {
                networkManager.sendMessageFromTheSever(createMessage(LOCAL_PLAYER_MANAGER_ID_ASSIGNED, playerID + ""), connection);
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, LOCAL_PLAYER_MANAGER_MESSAGE_PLAYER_IDS_ID, getPlayerIDs()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Player getLocalSelf() {
        if (NetworkManager.isServer(networkManager)) {
            return getPlayer(0).getPlayer();
        }
        return null; // player not found i.e. not properly initialized
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

    private String getPlayerIDs() {
        StringBuilder playerIDs = new StringBuilder();
        for (PlayerConnection p : getPlayers()) {
            playerIDs.append(p.getPlayerID()).append(":");
        }
        return playerIDs.toString();
    }

    public ArrayList<String> getPlayersIDs(){
        ArrayList<String> playerIDs = new ArrayList<>();
        for(PlayerConnection p: getPlayers()){
            playerIDs.add(Integer.toString(p.getPlayerID()));
        }
        while(playerIDs.size() < 5){
            playerIDs.add(null);
        }
        return playerIDs;
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
        if (playerConnections.contains(player) && networkManager != null && NetworkManager.isServer(networkManager)) {
            try {
                networkManager.sendMessageFromTheSever(createMessage(LOCAL_PLAYER_MANAGER_ID_PLAYER_DISCONNECT,player.getPlayerID()+""), player.getConnection());
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, LOCAL_PLAYER_MANAGER_MESSAGE_PLAYER_IDS_ID, getPlayerIDs()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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
        //this method gets executed, when callbacks are registered with the network manager
    }
}