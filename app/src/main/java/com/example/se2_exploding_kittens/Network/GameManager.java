package com.example.se2_exploding_kittens.Network;

import android.widget.Toast;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.cards.Card;

import java.util.ArrayList;

public class GameManager implements MessageCallback {

    private NetworkManager networkManager;
    private TurnManager turnManager;
    private int numberOfPlayers;
    private Deck deck;
    private PlayerManager playerManager;
    public static final int GAME_MANAGER_MESSAGE_ID = 500;

    public GameManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
        this.turnManager = new TurnManager(networkManager);
        long seed = System.currentTimeMillis();
        this.deck = new Deck(seed);
        this.playerManager = PlayerManager.getInstance();
        this.numberOfPlayers = turnManager.getNumberOfPlayers();
    }

    private void sendCard(int playerID, String cardName, boolean isInitialHand) {
        try {
            networkManager.sendMessageFromTheSever(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_ID, cardName), playerManager.getPlayer(playerID).getConnection());
            //checks if card was part of initial hand and if player has to draw another card
            if (!isInitialHand && (playerManager.getPlayer(playerID).numberOfTurnsLeft() == 0)) {
                turnManager.sendNextGameSateToClients();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void sendHand(int playerID, ArrayList<Card> firstHand, boolean isInitialHand) {
        for (Card card : firstHand) {
            sendCard(playerID, card.toString(), isInitialHand);
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {
        if(sender instanceof ServerTCPSocket){
            int playerID = playerManager.getPlayerIDByConnection((ServerTCPSocket) sender);

        }
    }
}
