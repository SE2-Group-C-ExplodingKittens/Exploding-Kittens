package com.example.se2_exploding_kittens.GameBoard;

public class GameBoard {
    public Deck deck;
    public Player player;
    public DiscardPile discardPile;

    public GameBoard() {
        deck = new Deck();
        discardPile = new DiscardPile();
        player = new Player();
        deck.createDeck();
        deck.shuffleDeck();
        for (int i = 0; i < 7; i++) {
            drawCard();
        }
    }

    public void drawCard() {
        if (!deck.isEmpty()) {
            player.addCard(deck.getFirstCard());
            deck.removeFirstCard();
        }
    }

    public void discardCard() {
        if (!player.isEmpty()) {
            discardPile.addCard(player.getLastCard());
            player.removeLastCard();
        }
    }
}
