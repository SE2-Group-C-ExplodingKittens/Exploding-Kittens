package com.example.se2_exploding_kittens;

import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.game_logic.Player;

public class TurnManager implements MessageCallback {
    public static final int TURN_MANAGER_MESSAGE_ID = 300;

    public static final int TURN_MANAGER_TURN_FINISHED = 1;
    public static final int TURN_MANAGER_ASSIGN_TURNS = 4;

    private final NetworkManager networkManager;
    private final PlayerManager playerManager;
    private int currentPlayerIndex;
    private int previousPlayerIndex;
    private int currentPlayerTurns;
    private int previousPlayerTurns;

    public TurnManager(NetworkManager networkManager) {
        networkManager.subscribeCallbackToMessageID(this, TURN_MANAGER_MESSAGE_ID);
        this.playerManager = PlayerManager.getInstance();
        this.networkManager = networkManager;
        this.currentPlayerTurns = 0;
        this.currentPlayerIndex = 0;
    }

    public void startGame() {
        if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
            shuffleOrder();
            currentPlayerTurns = 1;
            previousPlayerTurns = currentPlayerTurns;
            currentPlayerIndex = 0;
            previousPlayerIndex = 0;
            sendNextSateToPlayers();
        }
    }

    private void shuffleOrder() {
        playerManager.shuffle();
    }

    private String assembleGameStateMessage(int turns, int playerID) {
        return TurnManager.TURN_MANAGER_ASSIGN_TURNS + ":" + turns + ":" + playerID;
    }

    public void sendNextSateToPlayers() {
        if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
            PlayerConnection currentPlayerConnection = playerManager.getPlayer(currentPlayerIndex);
            playerManager.getPlayer(currentPlayerIndex).getPlayer().setPlayerTurns(currentPlayerTurns);
            //message will be = playerID:numberOfTurns
            String gameStateMessage = assembleGameStateMessage(currentPlayerTurns, currentPlayerConnection.getPlayerID());

            try {
                Message m = new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage);
                networkManager.sendMessageBroadcast(m);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void broadcastTurnFinished(Player player, NetworkManager networkManager) {
        String gameStateMessage = TURN_MANAGER_TURN_FINISHED + ":" + player.getPlayerId();
        try {
            Message m = new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage);
            networkManager.sendMessageBroadcast(m);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public void gameStateNextTurn(int turns) {
        previousPlayerTurns = currentPlayerTurns;
        currentPlayerTurns = turns;
        previousPlayerIndex = currentPlayerIndex;
        currentPlayerIndex = (currentPlayerIndex + 1) % playerManager.getPlayerSize();
        sendNextSateToPlayers();
    }

    private void handleMessage(int messageType, int turns, int playerID) {
        switch (messageType) {
            case TURN_MANAGER_TURN_FINISHED:
                previousPlayerTurns = currentPlayerTurns;
                currentPlayerTurns = 0;
                //sendNextSateToPlayers();
                if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                    playerManager.getPlayer(playerID).getPlayer().setPlayerTurns(turns);
                    //game manage call?
                }
                break;
            case TURN_MANAGER_ASSIGN_TURNS:
                if (playerManager.getLocalSelf().getPlayerId() == playerID) {
                    playerManager.getLocalSelf().setPlayerTurns(turns);
                }
                if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
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
}
