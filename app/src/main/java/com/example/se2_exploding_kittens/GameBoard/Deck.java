package com.example.se2_exploding_kittens.GameBoard;

import com.example.se2_exploding_kittens.cards.AbstractCard;
import com.example.se2_exploding_kittens.cards.GenericCard;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    ArrayList<AbstractCard> deck = new ArrayList<>();

    public void createDeck() {
        for (int i = 0; i < 52; i++) {
            this.deck.add(new GenericCard(i));
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.deck);
    }

    public int getDeckSize(){
        return deck.size();
    }
}
