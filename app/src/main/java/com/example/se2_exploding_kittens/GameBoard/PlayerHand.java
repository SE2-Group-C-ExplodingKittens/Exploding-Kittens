package com.example.se2_exploding_kittens.GameBoard;

import com.example.se2_exploding_kittens.cards.AbstractCard;

import java.util.ArrayList;

public class PlayerHand extends AbstractDeck {

    PlayerHand() {
        deck = new ArrayList<>();
    }

    public void removeFirstCard() {
        if (deck.size() > 0) {
            deck.remove(0);
        }
    }

    public AbstractCard getFirstCard() {
        if (!isEmpty()) {
            return deck.get(0);
        }
        return null;
    }
}
