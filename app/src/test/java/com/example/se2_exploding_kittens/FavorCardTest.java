package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.game_logic.cards.DefuseCard.DEFUSE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.FavorCard.FAVOR_CARD_ID;
import static org.mockito.Mockito.mock;

import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;
import com.example.se2_exploding_kittens.game_logic.cards.FavorCard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FavorCardTest {
    NetworkManager networkManager;
    DiscardPile discardPile;

    @BeforeEach
    public void setUp() {
        networkManager = mock(NetworkManager.class);
        discardPile = new DiscardPile();
    }

    @Test
    void testFutureHandling() {
        Player player = new Player();
        player.setPlayerTurns(1);
        player.setAlive(true);
        player.setPlayerId(0);
        player.addCardToHand(String.valueOf(FAVOR_CARD_ID));
        player.addCardToHand(String.valueOf(DEFUSE_CARD_ID));

        FavorCard fc = new FavorCard();
        fc.handleActions(player, networkManager, discardPile, null);

        Assertions.assertEquals(1, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof DefuseCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof FavorCard);
    }
}
