package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.game_logic.cards.AttackCard.ATTACK_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.DefuseCard.DEFUSE_CARD_ID;

import static org.mockito.Mockito.mock;

import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.AttackCard;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttackCardTest {
    NetworkManager networkManager;
    DiscardPile discardPile;

    @BeforeEach
    public void setUp() {
        networkManager = mock(NetworkManager.class);
        discardPile = new DiscardPile();
    }

    @Test
    void testAttackHandling() {
        Player player = new Player();
        player.setPlayerTurns(2);
        player.setAlive(true);
        player.setPlayerId(0);
        player.addCardToHand(String.valueOf(ATTACK_CARD_ID));
        player.addCardToHand(String.valueOf(DEFUSE_CARD_ID));

        NetworkManager networkManager = mock(NetworkManager.class);
        TurnManager turnManager = mock(TurnManager.class);

        AttackCard ac = new AttackCard();
        ac.handleActions(player, networkManager, discardPile, turnManager);

        Assertions.assertEquals(0, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof DefuseCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof AttackCard);
    }
}