package com.example.se2_exploding_kittens.GameBoard;

import com.example.se2_exploding_kittens.cards.AbstractCard;

import java.util.ArrayList;

public class AbstractDeck {
    ArrayList<AbstractCard> deck;

    public int getDeckSize() {
        return deck.size();
    }

    public void addCard(AbstractCard card) {
        this.deck.add(card);
    }

    public boolean isEmpty() {
        return getDeckSize() <= 0;
    }

    //provisional
    public String printDeck() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < deck.size(); i++) {
            str.append(deck.get(i).number).append(" ");
        }
        return str.toString();
    }
}
