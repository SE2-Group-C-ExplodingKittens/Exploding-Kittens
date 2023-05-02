package com.example.se2_exploding_kittens.cards.Deck;

import com.example.se2_exploding_kittens.cards.CardFactory;
import com.example.se2_exploding_kittens.cards.Cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    ArrayList<Cards> deck = new ArrayList<>();
    public CardFactory factory;
    public Deck() {
        initAttackCard();
        initBombCard();
        initCatCard();
        initDefuseCard();
        initFavorCard();
        initNopeCard();
        initShuffleCard();
        initSkipCard();
        Collections.shuffle(deck);
    }

    public ArrayList<Cards> getDeck() {
        return deck;
    }

    public void initAttackCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("ATTACKCARD"));
        }
    }
    public void initBombCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("BOMBCARD"));
        }
    }
    public void initCatCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("CATCARD"));
        }
    }
    public void initDefuseCard() {
        for (int i = 0; i < 6; i++) {
            deck.add(factory.getCard("DEFUSECARD"));
        }
    }
    public void initFavorCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("FAVORCARD"));
        }
    }
    public void initNopeCard() {
        for (int i = 0; i < 5; i++) {
            deck.add(factory.getCard("NOPECARD"));
        }
    }
    public void initShuffleCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("SHUFFLECARD"));
        }
    }
    public void initSkipCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("SKIPCARD"));
        }
    }

    public ArrayList<String> getCardName(){
        ArrayList<String> cardNames = new ArrayList<String>();
        for (Cards card : deck) {
            cardNames.add(card.getCardName());
        }
        return cardNames;
    }

    public void test() {
        Deck deck = new Deck();
        for (int i = 0; i < deck.getDeck().size(); i++) {
            System.out.println(deck.getDeck().indexOf(i));
        }

    }

}
