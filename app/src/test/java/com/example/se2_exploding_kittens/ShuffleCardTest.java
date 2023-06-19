package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.game_logic.cards.DefuseCard.DEFUSE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.ShuffleCard.SHUFFLE_CARD_ID;

import static org.mockito.Mockito.mock;

import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;
import com.example.se2_exploding_kittens.game_logic.cards.ShuffleCard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShuffleCardTest {
    NetworkManager networkManager;
    DiscardPile discardPile;

    @BeforeEach
    public void setUp() {
        networkManager = mock(NetworkManager.class);
        discardPile = new DiscardPile();
    }

    @Test
    void testShuffleHandling() {
        Player player = new Player();
        player.setPlayerTurns(1);
        player.setAlive(true);
        player.setPlayerId(0);
        player.addCardToHand(String.valueOf(SHUFFLE_CARD_ID));
        player.addCardToHand(String.valueOf(DEFUSE_CARD_ID));

        Deck deck = new Deck(1);

        NetworkManager networkManager = mock(NetworkManager.class);

        ShuffleCard sc = new ShuffleCard();
        sc.handleActions(player, networkManager, discardPile, deck);

        Assertions.assertEquals(1, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof DefuseCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof ShuffleCard);
    }
}