package com.example.se2_exploding_kittens.game_logic;

import com.example.se2_exploding_kittens.game_logic.cards.Card;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;

import java.util.ArrayList;

public class Player {
    int playerId;
    boolean alive = true;

    boolean canNope = false;

    public Player(int playerId) {
        this.playerId = playerId;
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
}
