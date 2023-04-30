package com.example.se2_exploding_kittens;

import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;

import java.util.ArrayList;
import java.util.Collections;

public class TurnManager implements MessageCallback {
    public static final int TURN_MANAGER_MESSAGE_ID = 300;
    private ArrayList<ServerTCPSocket> players;
    private NetworkManager networkManager;
    private int playerIndex;

    public TurnManager(ArrayList<ServerTCPSocket> players, NetworkManager networkManager) {
        networkManager.subscribeCallbackToMessageID(this,TURN_MANAGER_MESSAGE_ID);
        this.players = players;
        //this.players
        this.networkManager = networkManager;
        this.playerIndex = 0;
    }

    public void startGame() {
        shuffleOrder();
        sendGameSateToClients();
    }

    private void shuffleOrder() {
        Collections.shuffle(players);
        playerIndex = 0;
    }
    //1:1:2       ID1=players <n> Turn
    //2:2:2       ID1=players <n> Turn
    private void sendGameSateToClients() {
        String gameStateMessage = "It's " + players.get(playerIndex) + "s Turn.";

        try {
            networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void updateGameState() {
        playerIndex = (playerIndex + players.size() - 1) % players.size();
        sendGameSateToClients();
    }

    private ServerTCPSocket getCurrentPlayer() {
        return players.get(playerIndex);
    }

    public void handlePlayerAction(ServerTCPSocket player) {
        if (player != getCurrentPlayer()) {
            sendErrorMessageToPlayer(player, "It's not your turn.");
            return;
        }

        // Handle player action here

        updateGameState();
        sendGameSateToClients();
    }

    private void sendErrorMessageToPlayer(ServerTCPSocket player, String errorMessage) {
        try {
            //networkManager.sendMessageFromTheSever(new Message(MessageType.ERROR, TURN_MANAGER_MESSAGE_ID, errorMessage), plavermanger.getConnection(id));
            networkManager.sendMessageFromTheSever(new Message(MessageType.ERROR, TURN_MANAGER_MESSAGE_ID, errorMessage), player);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {
        //this runs on server
        if(sender instanceof ServerTCPSocket){
            //int player = playermanager.getPlayerID((serverTPCSocket)sender)
            String message = Message.parseAndExtractPayload(text);
        }
    }
}
