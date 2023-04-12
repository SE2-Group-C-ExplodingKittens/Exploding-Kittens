package com.example.se2_exploding_kittens.GameBoard;

import com.example.se2_exploding_kittens.cards.AbstractCard;

import java.util.ArrayList;
import java.util.Collections;

public class AbstractDeck {
    ArrayList<AbstractCard> deck;

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public int getDeckSize() {
        return deck.size();
    }

    public AbstractCard getFirstCard() {
        if (!isEmpty()) {
            return deck.get(0);
        }
        return null;
    }

    public AbstractCard getLastCard() {
        if (!isEmpty()) {
            return deck.get(getDeckSize() - 1);
        }
        return null;
    }

    public void removeFirstCard() {
        if (deck.size() > 0) {
            deck.remove(0);
        }
    }

    public void removeLastCard() {
        if (deck.size() > 0) {
            deck.remove(getLastCard());
        }
    }

    public void addCard(AbstractCard card) {
        this.deck.add(card);
    }

    public boolean isEmpty(){
        return getDeckSize()<=0;
    }

    public String printDeck(){
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < deck.size(); i++){
            str.append(deck.get(i).number).append(" ");
        }
        return str.toString();
    }
}
