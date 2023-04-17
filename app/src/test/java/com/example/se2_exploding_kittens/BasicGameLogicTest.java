package com.example.se2_exploding_kittens;

import com.example.se2_exploding_kittens.GameBoard.GameBoard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class BasicGameLogicTest {

    GameBoard gameBoard;

    @BeforeEach
    public void setUp() {
        gameBoard = new GameBoard();
    }

    @Test
    public void testInitialDraw() {
        Assertions.assertEquals(gameBoard.playerHand.getDeckSize(), 7);
    }

    @Test
    public void testDiscardCard() {
        gameBoard.discardCard();
        Assertions.assertEquals(gameBoard.discardPile.getDeckSize(), 1);
    }

    @Test
    public void testPlayCard() {
        gameBoard.playCard();
        Assertions.assertEquals(gameBoard.gameStack.getDeckSize(), 1);
    }

    @Test
    public void testPrintDeck() {
        int firstCard = gameBoard.playerHand.getFirstCard().number;
        gameBoard.discardCard();
        Assertions.assertEquals(gameBoard.discardPile.printDeck(), firstCard + " ");
    }
}
