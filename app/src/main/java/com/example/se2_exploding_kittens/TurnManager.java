package com.example.se2_exploding_kittens;

import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;

public class TurnManager implements MessageCallback {
    public static final int TURN_MANAGER_MESSAGE_ID = 300;
    private NetworkManager networkManager;
    private PlayerManager playerManager;
    private int currentPlayerIndex;

    public TurnManager(NetworkManager networkManager) {
        networkManager.subscribeCallbackToMessageID(this, TURN_MANAGER_MESSAGE_ID);
        this.playerManager = PlayerManager.getInstance();
        this.networkManager = networkManager;
        this.currentPlayerIndex = 0;
    }

    public void startGame() {
        shuffleOrder();
        currentPlayerIndex = 0;
        sendGameSateToClients();
    }

    private void shuffleOrder() {
        playerManager.shuffle();
    }

    private void sendGameSateToClients() {
        PlayerConnection currentPlayerConnection = playerManager.getPlayer(currentPlayerIndex);
        //provisional
        String gameStateMessage = Integer.toString(currentPlayerConnection.getPlayerID());

        try {
            networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void updateGameState() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playerManager.getPlayerSize();
        sendGameSateToClients();
    }

    public void handlePlayerAction(int playerID) {
        if (currentPlayerIndex != playerID) {
            //provisional
            sendErrorMessageToPlayer(playerID, "It's not your turn.");
            return;
        }

        updateGameState();
        sendGameSateToClients();
    }

    private void sendErrorMessageToPlayer(int playerID, String errorMessage) {
        try {
            networkManager.sendMessageFromTheSever(new Message(MessageType.ERROR, TURN_MANAGER_MESSAGE_ID, errorMessage), playerManager.getPlayer(playerID).getConnection());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {
        if (sender instanceof ServerTCPSocket) {
            int player = playerManager.getPlayerIDByConnection((ServerTCPSocket) sender);
            String message = Message.parseAndExtractPayload(text);
        }
    }

    public int getNumberOfPlayers(){
        return PlayerManager.getInstance().getPlayerSize();
    }
}
