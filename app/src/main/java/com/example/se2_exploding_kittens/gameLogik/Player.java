package com.example.se2_exploding_kittens.gameLogik;

import com.example.se2_exploding_kittens.gameLogik.cards.Card;

import java.util.ArrayList;

public class Player {
    int playerId;

    public Player(int playerId) {
        this.playerId = playerId;
    }

    public ArrayList<Card> hand = new ArrayList<>();
}
