package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.game_logic.GameLogic.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.BombCard;
import com.example.se2_exploding_kittens.game_logic.cards.Card;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;
import com.example.se2_exploding_kittens.game_logic.cards.SkipCard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


class GameLogicTest {

    PlayerManager playerManager;
    PlayerConnection pCon0;
    PlayerConnection pCon1;
    PlayerConnection pCon2;
    PlayerConnection pCon3;
    ArrayList<PlayerConnection> pcList;


    @BeforeEach
    public void setupUpTest() {
        playerManager = mock(PlayerManager.class);
        pCon0 = new PlayerConnection(null,0);
        pCon1 = new PlayerConnection(null,1);
        pCon2 = new PlayerConnection(null,2);
        pCon3 = new PlayerConnection(null,3);
        pcList = new ArrayList<PlayerConnection>();

    }

    @Test
    void testCheckForWinner() {
        pcList.add(pCon0);
        pcList.add(pCon1);
        pcList.add(pCon2);
        pcList.add(pCon3);
        pCon0.getPlayer().setAlive(true);
        pCon1.getPlayer().setAlive(true);
        pCon2.getPlayer().setAlive(true);
        pCon3.getPlayer().setAlive(true);

        when(playerManager.getPlayerSize()).thenReturn(4);
        when(playerManager.getPlayers()).thenReturn(pcList);
        Assertions.assertEquals(-1, checkForWinner(playerManager));

        pCon3.getPlayer().setAlive(false);
        Assertions.assertEquals(-1, checkForWinner(playerManager));

        pCon2.getPlayer().setAlive(false);
        Assertions.assertEquals(-1, checkForWinner(playerManager));

        pCon1.getPlayer().setAlive(false);
        Assertions.assertEquals(0, checkForWinner(playerManager));
    }

    @Test
    void testCheckForWinnerWithOnlyOnePlayer() {
        pcList.add(pCon0);
        pCon0.getPlayer().setAlive(true);

        when(playerManager.getPlayerSize()).thenReturn(1);
        when(playerManager.getPlayers()).thenReturn(pcList);
        Assertions.assertEquals(0, checkForWinner(playerManager));
    }

    @Test
    void testFinishTurn() {
        Player player = new Player();
        NetworkManager networkManager = mock(NetworkManager.class);
        TurnManager turnManager = mock(TurnManager.class);
        int futureTurns = 1;

        when(networkManager.getConnectionRole()).thenReturn(TypeOfConnectionRole.SERVER);

        finishTurn(player, networkManager, futureTurns, turnManager);

        verify(networkManager, times(1)).getConnectionRole();
        verify(turnManager, times(1)).gameStateNextTurn(futureTurns);
    }

    @Test
    void testFinishTurnNotCalled() {
        Player player = new Player();
        NetworkManager networkManager = mock(NetworkManager.class);
        TurnManager turnManager = mock(TurnManager.class);
        int futureTurns = 1;

        when(networkManager.getConnectionRole()).thenReturn(TypeOfConnectionRole.CLIENT);

        finishTurn(player, networkManager, futureTurns, turnManager);

        verify(networkManager, times(1)).getConnectionRole();
        verify(turnManager, times(0)).gameStateNextTurn(futureTurns);
    }

    @Test
    void testCanCardBePulled() {
        Player player = new Player();
        player.setAlive(true);
        player.setPlayerTurns(2);
        Assertions.assertTrue(canCardBePulled(player));

        player.setHasWon(true);
        Assertions.assertFalse(canCardBePulled(player));

        player.setHasWon(false);
        player.setPlayerTurns(1);
        Assertions.assertTrue(canCardBePulled(player));
        player.setPlayerTurns(0);
        Assertions.assertFalse(canCardBePulled(player));
        player.setAlive(false);
        Assertions.assertFalse(canCardBePulled(player));
    }

    @Test
    void testCanCardBePlayedCardDefuse() {
        Player player = new Player();
        player.setAlive(true);
        player.setHasBomb(false);
        player.setPlayerTurns(1);
        Assertions.assertFalse(canCardBePlayed(player, new DefuseCard()));
        player.setPlayerTurns(0);
        Assertions.assertFalse(canCardBePlayed(player, new DefuseCard()));
        player.setHasBomb(true);
        Assertions.assertTrue(canCardBePlayed(player, new DefuseCard()));
        player.setAlive(false);
        Assertions.assertFalse(canCardBePlayed(player, new DefuseCard()));
    }

    @Test
    void testCanCardBePlayedCardSkip() {
        Player player = new Player();
        player.setAlive(true);

        player.setPlayerTurns(0);
        Assertions.assertFalse(canCardBePlayed(player, new SkipCard()));
        player.setPlayerTurns(2);
        Assertions.assertTrue(canCardBePlayed(player, new SkipCard()));

        player.setHasWon(true);
        Assertions.assertFalse(canCardBePulled(player));
        player.setHasWon(false);

        player.setPlayerTurns(1);
        Assertions.assertTrue(canCardBePlayed(player, new SkipCard()));
        player.setHasBomb(true);
        Assertions.assertFalse(canCardBePlayed(player, new SkipCard()));
        player.setAlive(false);
        Assertions.assertFalse(canCardBePlayed(player, new SkipCard()));
        player.setHasBomb(false);
        Assertions.assertFalse(canCardBePlayed(player, new SkipCard()));
    }

    @Test
    void testCardHasBeenPlayed() {
        Player player = new Player();
        player.setAlive(true);
        player.setPlayerId(0);
        Card card = mock(DefuseCard.class);
        NetworkManager networkManager = mock(NetworkManager.class);
        DiscardPile discardPile = mock(DiscardPile.class);
        TurnManager turnManager = mock(TurnManager.class);
        Deck deck = mock(Deck.class);

        cardHasBeenPlayed(player,card,networkManager,discardPile, turnManager, deck,null);
        verify(((DefuseCard) card), times(1)).handleActions(player,networkManager,discardPile,turnManager, deck);

        card = mock(SkipCard.class);
        cardHasBeenPlayed(player,card,networkManager,discardPile, turnManager, deck, null);
        verify(((SkipCard) card), times(1)).handleActions(player,networkManager,discardPile,turnManager);

    }

    @Test
    void testcardHasBeenPulled() {
        Player player = new Player();
        player.setAlive(true);
        player.setPlayerId(0);

        Card card = mock(DefuseCard.class);
        NetworkManager networkManager = mock(NetworkManager.class);
        DiscardPile discardPile = mock(DiscardPile.class);
        TurnManager turnManager = mock(TurnManager.class);
        player.setPlayerTurns(2);

        cardHasBeenPulled(player, card, networkManager, discardPile, turnManager);
        Assertions.assertEquals(1, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(player.getHand().size() - 1) instanceof DefuseCard);

        card = mock(BombCard.class);
        cardHasBeenPulled(player, card, networkManager, discardPile, turnManager);
        Assertions.assertEquals(0, player.getPlayerTurns());
        verify(((BombCard) card), times(1)).handleActions(player,networkManager,discardPile,turnManager);
    }

}
