package com.example.se2_exploding_kittens.GameBoard;

public class GameBoard {
    public Deck deck;
    public PlayerHand playerHand;
    public DiscardPile discardPile;
    public GameStack gameStack;

    public GameBoard() {
        deck = new Deck();
        discardPile = new DiscardPile();
        gameStack = new GameStack();
        playerHand = new PlayerHand();
        deck.createDeck();
        deck.shuffleDeck();
        for (int i = 0; i < 7; i++) {
            drawCard();
        }
    }

    public void drawCard() {
        if (!deck.isEmpty()) {
            playerHand.addCard(deck.getFirstCard());
            deck.removeFirstCard();
        }
    }

    public void discardCard() {
        if (!playerHand.isEmpty()) {
            discardPile.addCard(playerHand.getFirstCard());
            playerHand.removeFirstCard();
        }
    }

    public void playCard() {
        if (!playerHand.isEmpty()) {
            gameStack.addCard(playerHand.getFirstCard());
            playerHand.removeFirstCard();
        }
    }
}
