package com.example.se2_exploding_kittens.Network;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.cards.Cards;
import com.example.se2_exploding_kittens.cards.Deck.Deck;

import java.util.ArrayList;

public class GameManager {

    NetworkManager networkManager;
    TurnManager turnManager;
    private int numberOfPlayers;
    Deck deck;
    PlayerManager playerManager;
    public static final int GAME_MANAGER_MESSAGE_ID = 500;

    public GameManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
        this.turnManager = new TurnManager(networkManager);
        this.deck = new Deck();
        this.playerManager = PlayerManager.getInstance();
        numberOfPlayers = turnManager.getNumberOfPlayers();
    }

    private void sendCard(int playerID, String cardName) {
        try {
            networkManager.sendMessageFromTheSever(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_ID, cardName), playerManager.getPlayer(playerID).getConnection());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void sendCards(int playerID, ArrayList<Cards> firstHand) {
        for (Cards card : firstHand) {
            sendCard(playerID, card.toString());
        }
    }
}
