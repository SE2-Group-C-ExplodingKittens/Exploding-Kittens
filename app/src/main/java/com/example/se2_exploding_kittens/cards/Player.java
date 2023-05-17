package com.example.se2_exploding_kittens.cards;

import java.util.ArrayList;

public class Player {

    private int playerTurns;
    private int playerId;
    private boolean playerTurn;

    ArrayList<Player> currentPlayersOrder;

    public Player(int playerId) {
        this.playerTurns = 1;
        this.playerId = playerId;
    }

    //    Implement attack card
    public void playAttack() {
        // Find the next player in the turn order
        Player nextPlayer = findNextPlayer(currentPlayersOrder);
        // Increase the number of turns for the next player
        nextPlayer.setPlayerTurns(2);
    }

    public Player findNextPlayer(ArrayList<Player> players) {
        //  Assuming that the Players will be stored in an ArrayList
        int currIndex = players.indexOf(this);
        int nextIndex = (currIndex + 1) % players.size();
        return players.get(nextIndex);
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

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
