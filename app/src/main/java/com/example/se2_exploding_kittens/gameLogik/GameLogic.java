package com.example.se2_exploding_kittens.gameLogik;

import java.util.ArrayList;

public class GameLogic {

    ArrayList<Player> playerList = new ArrayList<>();
    Deck deck = new Deck();

    public boolean cheat(int playerID){
        for(Player player : playerList){
            if(playerID == player.playerId){
                player.playerHand.add(deck.getNextCard()); //Theoretically this could throw and exception, but practically this must not happen, so I am not catching it.
                return true;
            }
        }
        return false;
    }

}
