package com.example.se2_exploding_kittens.GameBoard;

import com.example.se2_exploding_kittens.cards.GenericCard;

import java.util.ArrayList;

public class Deck extends AbstractDeck {

    Deck() {
        deck = new ArrayList<>();
    }

    // initialize card c, x amount of times (x = depending on number of players)
    public void createDeck() {
        for (int i = 0; i < 10; i++) {
            deck.add(new GenericCard(i));
        }
    }
}
