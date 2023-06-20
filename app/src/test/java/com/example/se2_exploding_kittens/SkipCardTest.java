package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.game_logic.cards.DefuseCard.DEFUSE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.SkipCard.SKIP_CARD_ID;
import static org.mockito.Mockito.mock;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;
import com.example.se2_exploding_kittens.game_logic.cards.SkipCard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SkipCardTest {
    NetworkManager networkManager;
    DiscardPile discardPile;

    @BeforeEach
    public void setupUpTest() {
        networkManager = mock(NetworkManager.class);
        discardPile = new DiscardPile();
    }

    @Test
    void testSkipHandleActions() {
        Player player = new Player();
        player.setPlayerTurns(2);
        player.setAlive(true);
        player.setPlayerId(1);
        player.addCardToHand(String.valueOf(SKIP_CARD_ID));
        player.addCardToHand(String.valueOf(SKIP_CARD_ID));
        player.addCardToHand(String.valueOf(DEFUSE_CARD_ID));

        NetworkManager networkManager = mock(NetworkManager.class);
        TurnManager turnManager = mock(TurnManager.class);

        SkipCard sc = new SkipCard();
        sc.handleActions(player, networkManager, discardPile, turnManager);

        Assertions.assertEquals(1, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof SkipCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof SkipCard);

        sc.handleActions(player, networkManager, discardPile, turnManager);

        Assertions.assertEquals(0, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof DefuseCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof SkipCard);

    }
}
