package com.example.se2_exploding_kittens.cards;

import java.util.ArrayList;

public class Player {

    int playerTurns;
    int playerId;
    boolean playerTurn;

    public Player(int playerId) {
        this.playerId = playerId;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    ArrayList<Cards> playerHand;


    public Player() {
        playerTurns = 1;
    }

    public ArrayList<Cards> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(ArrayList<Cards> playerHand) {
        this.playerHand = playerHand;
    }

    public void setPlayerTurns(int numTurns) {
        playerTurns = numTurns;
    }


}
