package com.example.se2_exploding_kittens;

import com.example.se2_exploding_kittens.Network.DisconnectedCallback;
import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.game_logic.Player;

public class TurnManager implements MessageCallback, DisconnectedCallback {
    public static final int TURN_MANAGER_MESSAGE_ID = 300;

    public static final int LOCAL_TURN_MANAGER_TURN_FINISHED = 1;
    public static final int LOCAL_TURN_MANAGER_ASSIGN_TURNS = 4;

    private NetworkManager networkManager;
    private PlayerManager playerManager;
    private int currentPlayerID;
    private int currentPlayerIDX;
    private int previousPlayerID;
    private int previousPlayerIDX;
    private int currentPlayerTurns;
    private int previousPlayerTurns;


    public TurnManager(NetworkManager networkManager) {
        networkManager.subscribeCallbackToMessageID(this, TURN_MANAGER_MESSAGE_ID);
        this.playerManager = PlayerManager.getInstance();
        this.networkManager = networkManager;
        networkManager.subscribeToDisconnectedCallback(this);
        this.currentPlayerTurns = 0;
        this.currentPlayerID = 0;
    }

    public void startGame() {
        if(NetworkManager.isServer(networkManager)){
            shuffleOrder();
            currentPlayerTurns = 1;
            previousPlayerTurns = currentPlayerTurns;
            currentPlayerIDX = 0;
            currentPlayerID = getNextPlayerID();
            previousPlayerID = currentPlayerID;
            previousPlayerIDX = currentPlayerIDX;
            int maxTries = 5;
            while (!sendNextSateToPlayers() && maxTries > 0){
                currentPlayerID = getNextPlayerID();
                maxTries--;
            }
        }
    }

    public int getCurrentPlayerTurns() {
        return currentPlayerTurns;
    }

    public void setCurrentPlayerTurns(int turns) {
        previousPlayerTurns = currentPlayerTurns;
        currentPlayerTurns = turns;
    }

    private void shuffleOrder() {
        playerManager.shuffle();
    }

    private String assembleGameStateMessage(int messageType, int turns, int playerID) {
        return messageType + ":" + turns + ":" + playerID;
    }

    public boolean sendNextSateToPlayers() {
        if(NetworkManager.isServer(networkManager)){
            PlayerConnection currentPlayerConnection = playerManager.getPlayer(currentPlayerID);
            if(currentPlayerConnection != null){
                currentPlayerConnection.getPlayer().setPlayerTurns(currentPlayerTurns);
                String gameStateMessage = assembleGameStateMessage(LOCAL_TURN_MANAGER_ASSIGN_TURNS, currentPlayerTurns, currentPlayerConnection.getPlayerID());

                try {
                    Message m = new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage);
                    networkManager.sendMessageBroadcast(m);
                    return true;
                } catch (IllegalAccessException e) {
                    return false;
                }
            }
            //message will be = playerID:numberOfTurns
        }
        return false;
    }

    public static void broadcastTurnFinished(Player player, NetworkManager networkManager) {
        String gameStateMessage = LOCAL_TURN_MANAGER_TURN_FINISHED + ":" + player.getPlayerId();
        try {
            Message m = new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage);
            networkManager.sendMessageBroadcast(m);
        } catch (IllegalAccessException e) {
            //this was called without proper network initialisation
        }
    }

    private int getNextPlayerID(){
        previousPlayerID = currentPlayerID;
        previousPlayerIDX = currentPlayerIDX;
        int tries = playerManager.getPlayerSize();
        do {
            currentPlayerIDX = (currentPlayerIDX + 1) % playerManager.getPlayerSize();
            tries--;
            //get next player if the player isn't alive
        }while (!playerManager.getPlayerByIndex(currentPlayerIDX).getPlayer().isAlive() && tries > 0);

        return playerManager.getPlayerByIndex(currentPlayerIDX).getPlayerID();
    }

    //this method is needed, so when going to the previous state, the old one gets set to zero
    private void setPreviousPlayerToZero(){
        if(NetworkManager.isServer(networkManager)){
            PlayerConnection previousPlayerConnection = playerManager.getPlayer(previousPlayerID);
            if(previousPlayerConnection != null){
                previousPlayerConnection.getPlayer().setPlayerTurns(0);
            }
        }
    }

    public void gameStateNextTurn(int turns) {
        previousPlayerTurns = currentPlayerTurns;
        currentPlayerTurns = turns;
        if(playerManager.getPlayerSize() > 1){
            currentPlayerID = getNextPlayerID();
        }
        int maxTries = 5;
        while (!sendNextSateToPlayers() && maxTries > 0){
            currentPlayerID = getNextPlayerID();
            maxTries--;
        }
    }

    public void resumePreviousGameState() {
        int tempPlayerIndex = currentPlayerIDX;
        currentPlayerIDX = previousPlayerIDX;
        previousPlayerIDX = tempPlayerIndex;
        int tempPlayerId = currentPlayerID;
        currentPlayerID = previousPlayerID;
        previousPlayerID = tempPlayerId;
        int tempTurns = currentPlayerTurns;
        currentPlayerTurns = previousPlayerTurns;
        previousPlayerTurns = tempTurns;
        if(sendNextSateToPlayers()){
            setPreviousPlayerToZero();
        }
    }

    public int getPlayerTurns(int playerID) {
        if (playerID == currentPlayerID) {
            return currentPlayerTurns;
        } else {
            return 0;
        }
    }


    private void handleMessage(int messageType, int turns, int playerID) {
        switch (messageType) {
            case LOCAL_TURN_MANAGER_TURN_FINISHED:
                previousPlayerTurns = currentPlayerTurns;
                currentPlayerTurns = 0;
                if(NetworkManager.isServer(networkManager)){
                    playerManager.getPlayer(playerID).getPlayer().setPlayerTurns(turns);
                    //game manage call?
                }
                break;
            case LOCAL_TURN_MANAGER_ASSIGN_TURNS:
                if (playerManager.getLocalSelf().getPlayerId() == playerID) {
                    playerManager.getLocalSelf().setPlayerTurns(turns);
                }
                if(NetworkManager.isServer(networkManager)){
                    playerManager.getPlayer(playerID).getPlayer().setPlayerTurns(turns);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {

        if (Message.parseAndExtractMessageID(text) == TURN_MANAGER_MESSAGE_ID) {
            if (sender == this) {
                //self message from "server to server" or a message from server to client
                String[] message = Message.parseAndExtractPayload(text).split(":");
                if (message.length >= 3) {
                    handleMessage(Integer.parseInt(message[0]), Integer.parseInt(message[1]), Integer.parseInt(message[2]));
                }
            }
            if (sender instanceof ServerTCPSocket || sender == this) {
                //dismiss if wrong player sends turn finished
                String[] message = Message.parseAndExtractPayload(text).split(":");
                if (message.length == 2) {
                    handleMessage(Integer.parseInt(message[0]), 0, Integer.parseInt(message[1]));
                }


            }
        }

    }

    public int getNumberOfPlayers() {
        return PlayerManager.getInstance().getPlayerSize();
    }

    public int getCurrentPlayerID() {
        return currentPlayerID;
    }

    private boolean isPlayerStillConnected(int playerID){
        return playerManager.getPlayer(playerID) != null;
    }

    private void playerDisconnected(ServerTCPSocket connection) {
        int playerID = playerManager.getPlayerIDByConnection(connection);
        if(playerID != -1){
            //a duplicate call shouldn't matter
            playerManager.connectionDisconnected(connection);
        }
        //disable nope, since it can get confused when a player leaves
        GameManager.sendNopeDisabled(networkManager);

        //go to the next player, either the disconnected doesn't exist anymore (-1) or has just disconnected
        if(playerID == -1 && isPlayerStillConnected(currentPlayerID)){
            previousPlayerTurns = currentPlayerTurns;

        }
        if(playerID == currentPlayerID){
            previousPlayerTurns = 1;
            currentPlayerTurns = 1;
            if(playerManager.getPlayerSize() > 1){
                currentPlayerID = getNextPlayerID();
                previousPlayerID = currentPlayerID;
            }
            int maxTries = 5;
            while (!sendNextSateToPlayers() && maxTries > 0){
                currentPlayerID = getNextPlayerID();
                maxTries--;
            }
        }

    }

    @Override
    public void connectionDisconnected(Object connection) {
        if (connection instanceof ServerTCPSocket) {
            playerDisconnected((ServerTCPSocket) connection);
        }
    }
}
