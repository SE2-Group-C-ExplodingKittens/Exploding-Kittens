package com.example.se2_exploding_kittens.gameLogik;

import com.example.se2_exploding_kittens.gameLogik.cards.Card;
import com.example.se2_exploding_kittens.gameLogik.cards.DefuseCard;

import java.util.ArrayList;

public class Player {
    int playerId;
    boolean alive = true;

    public Player(int playerId) {
        this.playerId = playerId;
    }

    public ArrayList<Card> hand = new ArrayList<>();

    public int getDefuse() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) instanceof DefuseCard) {
                return i;
            }
        }
        return -1;
    }
}
