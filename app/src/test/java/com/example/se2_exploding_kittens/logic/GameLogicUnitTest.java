package com.example.se2_exploding_kittens.logic;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;

import org.junit.Assert;
import org.junit.Test;

public class GameLogicUnitTest {

    Deck deck1 = new Deck(420420);

    @Test
    public void GameLogicTest() {
        GameLogic newGame = new GameLogic(3, 1, deck1, new GameManager(NetworkManager.getInstance(), deck1, new DiscardPile()), new TurnManager(NetworkManager.getInstance()));
        Assert.assertEquals(3, newGame.getPlayerList().size());
        Assert.assertEquals(1, newGame.getIdOfLocalPlayer());
        Assert.assertThrows(IllegalArgumentException.class, () ->
                new GameLogic(1, 1, deck1, new GameManager(NetworkManager.getInstance(), deck1, new DiscardPile()), new TurnManager(NetworkManager.getInstance()))
        );
        Assert.assertThrows(IllegalArgumentException.class, () ->
                new GameLogic(7, 1, deck1, new GameManager(NetworkManager.getInstance(), deck1, new DiscardPile()), new TurnManager(NetworkManager.getInstance()))
        );
        Assert.assertThrows(IllegalArgumentException.class, () ->
                new GameLogic(2, -1, deck1, new GameManager(NetworkManager.getInstance(), deck1, new DiscardPile()), new TurnManager(NetworkManager.getInstance()))
        );
        Assert.assertThrows(IllegalArgumentException.class, () ->
                new GameLogic(2, 3, deck1, new GameManager(NetworkManager.getInstance(), deck1, new DiscardPile()), new TurnManager(NetworkManager.getInstance()))
        );
    }
}
