package com.example.se2_exploding_kittens.game_logic;

import com.example.se2_exploding_kittens.game_logic.cards.Card;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;

import java.util.ArrayList;

public class Player {

    int playerId;
    boolean alive = true;
    boolean canNope = false;
    private int playerTurns;
    private boolean playerTurn;

    boolean alive = true;

    boolean canNope = false;

    // if the client initalizes a player object, ID may NOT be known yet thus getter and setter may be needed
    public Player(int playerId) {
        this.playerId = playerId;
    }

    private final ArrayList<Cards> hand = new ArrayList<>();

    public int getDefuse() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) instanceof DefuseCard) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Cards> getHand() {
        return hand;
    }

    private final ArrayList<Card> hand = new ArrayList<>();

    public int getDefuse() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) instanceof DefuseCard) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Card> getHand(){
        return hand;
    }

    public void setPlayerTurns(int numTurns) {
        playerTurns = numTurns;
    }

    public int getPlayerTurns() {
        return playerTurns;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    public int getPlayerId() {
        return playerId;
    }
}
