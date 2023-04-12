package com.example.se2_exploding_kittens.GameBoard;

public class GameBoard {

    public Deck deck;

    public GameBoard() {
        deck = new Deck();
        deck.createDeck();
        deck.shuffleDeck();
    }
}
