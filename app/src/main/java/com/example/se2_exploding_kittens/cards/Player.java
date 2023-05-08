package com.example.se2_exploding_kittens.cards;

import java.util.ArrayList;

public class Player {

    int playerTurns;
    int playerId;
    boolean playerTurn;


    ArrayList<Player> currentPlayersOrder;

    public Player() {
        playerTurns = 1;
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




}
