package com.example.se2_exploding_kittens.cards;

import java.util.ArrayList;

public class Player {

    private int playerTurns;
    private int playerId;
    private boolean playerTurn;

    boolean alive = true;

    boolean canNope = false;

    public Player(int playerId) {
        this.playerTurns = 1;
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
