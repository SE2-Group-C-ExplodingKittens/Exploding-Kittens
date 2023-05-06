package com.example.se2_exploding_kittens.GameBoard;

import com.example.se2_exploding_kittens.cards.AbstractCard;

import java.util.ArrayList;

public class DiscardPile extends AbstractDeck {


    private ArrayList<AbstractCard> typesOfCards = new ArrayList<AbstractCard>();
    DiscardPile() {
        deck = new ArrayList<>();
    }

    public ArrayList<AbstractCard> getCardTypes(){
        return typesOfCards;
    }

    public AbstractCard pullTypeOFCard(AbstractCard desiredCard) {
        // TODO Check if the card exists, remove from deck potentially from type of cards
        return null;
    }

    @Override
    public void addCard(AbstractCard card){
        // TODO check if the card already exists and after that you can add the card
        this.deck.add(card);
    }




}


