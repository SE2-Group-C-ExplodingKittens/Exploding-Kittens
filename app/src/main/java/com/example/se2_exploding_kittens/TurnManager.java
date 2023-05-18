package com.example.se2_exploding_kittens;

import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;

public class TurnManager implements MessageCallback {
    public static final int TURN_MANAGER_MESSAGE_ID = 300;

    public static final int TURN_MANAGER_CARD_PULLED = 1;
    public static final int TURN_MANAGER_ACTION_CARD_PLAYED = 2;
    public static final int TURN_MANAGER_NOPE_PLAYED = 3;
    public static final int TURN_MANAGER_ASSIGN_TURNS = 4;

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
        sendNextGameSateToClients();
    }

    private void shuffleOrder() {
        playerManager.shuffle();
    }

    public void sendNextGameSateToClients() {
        PlayerConnection currentPlayerConnection = playerManager.getPlayer(currentPlayerIndex);
        //message will be = playerID:numberOfTurns
        String gameStateMessage = Integer.toString(currentPlayerConnection.getPlayerID());

        try {
            networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void updateGameState() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playerManager.getPlayerSize();
        sendNextGameSateToClients();
    }

    public void handlePlayerAction(int playerID) {
        if (currentPlayerIndex != playerID) {
            //provisional error message
            sendErrorMessageToPlayer(playerID, "It's not your turn.");
            return;
        }

        updateGameState();
        sendNextGameSateToClients();
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

    public int getNumberOfPlayers() {
        return PlayerManager.getInstance().getPlayerSize();
    }
}
