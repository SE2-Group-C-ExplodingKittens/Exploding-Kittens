package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.game_logic.GameLogic.canCardBePlayed;
import static com.example.se2_exploding_kittens.game_logic.GameLogic.canCardBePulled;
import static com.example.se2_exploding_kittens.game_logic.GameLogic.cardHasBeenPlayed;
import static com.example.se2_exploding_kittens.game_logic.GameLogic.cardHasBeenPulled;
import static com.example.se2_exploding_kittens.game_logic.GameLogic.checkForWinner;
import static com.example.se2_exploding_kittens.game_logic.GameLogic.finishTurn;
import static com.example.se2_exploding_kittens.game_logic.GameLogic.getPlayerIDList;
import static com.example.se2_exploding_kittens.game_logic.GameLogic.setPlayerIDList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.AttackCard;
import com.example.se2_exploding_kittens.game_logic.cards.BombCard;
import com.example.se2_exploding_kittens.game_logic.cards.Card;
import com.example.se2_exploding_kittens.game_logic.cards.CatFiveCard;
import com.example.se2_exploding_kittens.game_logic.cards.CatFourCard;
import com.example.se2_exploding_kittens.game_logic.cards.CatOneCard;
import com.example.se2_exploding_kittens.game_logic.cards.CatThreeCard;
import com.example.se2_exploding_kittens.game_logic.cards.CatTwoCard;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;
import com.example.se2_exploding_kittens.game_logic.cards.FavorCard;
import com.example.se2_exploding_kittens.game_logic.cards.NopeCard;
import com.example.se2_exploding_kittens.game_logic.cards.SeeTheFutureCard;
import com.example.se2_exploding_kittens.game_logic.cards.ShuffleCard;
import com.example.se2_exploding_kittens.game_logic.cards.SkipCard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


class GameLogicTest {

    PlayerManager playerManager;
    PlayerConnection pCon0;
    PlayerConnection pCon1;
    PlayerConnection pCon2;
    PlayerConnection pCon3;
    CopyOnWriteArrayList<PlayerConnection> pcList;


    @BeforeEach
    public void setupUpTest() {
        playerManager = mock(PlayerManager.class);
        pCon0 = new PlayerConnection(null, 0);
        pCon1 = new PlayerConnection(null, 1);
        pCon2 = new PlayerConnection(null, 2);
        pCon3 = new PlayerConnection(null, 3);
        pcList = new CopyOnWriteArrayList<>();

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

        cardHasBeenPlayed(player, card, networkManager, discardPile, turnManager, deck, null);
        verify(((DefuseCard) card), times(1)).handleActions(player, networkManager, discardPile, turnManager, deck);

        card = mock(SkipCard.class);
        cardHasBeenPlayed(player, card, networkManager, discardPile, turnManager, deck, null);
        verify(((SkipCard) card), times(1)).handleActions(player, networkManager, discardPile, turnManager);

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
        verify(((BombCard) card), times(1)).handleActions(player, networkManager, discardPile, turnManager);
    }

    @Test
    void testGameLogic() {
        GameLogic test1 = new GameLogic(3, 0, new Deck(428522));
        Assertions.assertEquals(3, test1.getNumOfPlayer());
        Assertions.assertEquals(0, test1.getIdOfLocalPlayer());
        Assertions.assertEquals(428522, test1.getDeck().getSeed());

        Assertions.assertThrows(IllegalArgumentException.class, () -> new GameLogic(1, 0, new Deck(420420)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new GameLogic(6, 0, new Deck(420420)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new GameLogic(3, 3, new Deck(420420)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new GameLogic(3, -1, new Deck(420420)));

    }

    @Test
    void testsetPlayerIDList() {
        setPlayerIDList("21:WILLIAM:38:JAX");
        Assertions.assertEquals("21", getPlayerIDList().get(0));
        Assertions.assertEquals("WILLIAM", getPlayerIDList().get(1));
        Assertions.assertEquals("38", getPlayerIDList().get(2));
        Assertions.assertEquals("JAX", getPlayerIDList().get(3));
        Assertions.assertNull(getPlayerIDList().get(4));
        Assertions.assertEquals(5, getPlayerIDList().size());

        setPlayerIDList("271:Jax:7:>Jax");
        Assertions.assertEquals("271", getPlayerIDList().get(0));
        Assertions.assertEquals("Jax", getPlayerIDList().get(1));
        Assertions.assertEquals("7", getPlayerIDList().get(2));
        Assertions.assertEquals(">Jax", getPlayerIDList().get(3));
        Assertions.assertNull(getPlayerIDList().get(4));
        Assertions.assertEquals(5, getPlayerIDList().size());
    }

    @Test
    void testDeck() {
        Deck test1 = new Deck(42042069);
        Deck test2 = new Deck(42042069);

        for (int i = 0; i < 46; i++){
            Assertions.assertEquals(test1.getNextCard().getCardID(), test2.getNextCard().getCardID());
        }
        Assertions.assertThrows(IndexOutOfBoundsException.class, test1::getNextCard);
        Assertions.assertThrows(IndexOutOfBoundsException.class, test2::getNextCard);

        ArrayList<Integer> test3 = new ArrayList<>();
        ArrayList<Integer> test4 = new ArrayList<>();

        Deck testDeck1 = new Deck(4444);
        Deck testDeck2 = new Deck(5555);

        for(int i = 0; i < 46; i++){
            test3.add(testDeck1.getNextCard().getCardID());
            test4.add(testDeck2.getNextCard().getCardID());
        }

        Assertions.assertNotEquals(test3, test4);

    }

    @Test
    void testDealCards(){
        Deck test1 = new Deck(5545);
        ArrayList<Player> player = new ArrayList<>();
        player.add(new Player());
        player.add(new Player());

        test1.dealCards(player);

        Assertions.assertEquals(8,( (Player) player.get(0)).getHand().size());
        Assertions.assertEquals(8,( (Player) player.get(1)).getHand().size());

        Assertions.assertEquals(((Player) player.get(0)).getHand().get(0).getCardID(), DefuseCard.DEFUSE_CARD_ID);
        Assertions.assertEquals(((Player) player.get(1)).getHand().get(0).getCardID(), DefuseCard.DEFUSE_CARD_ID);

        ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0; i < 37; i++){
            array.add(test1.getNextCard().getCardID());
        }

        Assertions.assertThrows(IndexOutOfBoundsException.class, test1::getNextCard);

        for(int i = 0; i < 8; i++){
            array.add(((Player) player.get(0)).getHand().get(i).getCardID());
            array.add(((Player) player.get(1)).getHand().get(i).getCardID());
        }

        int amountBomb = 0;
        int amountSkip = 0;
        int amountFavour = 0;
        int amountCatCard1 = 0;
        int amountCatCard2 = 0;
        int amountCatCard3 = 0;
        int amountCatCard4 = 0;
        int amountCatCard5 = 0;
        int amountNope = 0;
        int amountFuture = 0;
        int amountShuffle = 0;
        int amountDefuse = 0;
        int amountAttack = 0;

        for(int i = 0; i < array.size(); i++){
            switch (array.get(i)){
                case BombCard.BOMB_CARD_ID:
                    amountBomb++;
                    break;
                case SkipCard.SKIP_CARD_ID:
                    amountSkip++;
                    break;
                case FavorCard.FAVOR_CARD_ID:
                    amountFavour++;
                    break;
                case CatOneCard.CAT_ONE_CARD_ID:
                    amountCatCard1++;
                    break;
                case CatTwoCard.CAT_TWO_CARD_ID:
                    amountCatCard2++;
                    break;
                case CatThreeCard.CAT_THREE_CARD_ID:
                    amountCatCard3++;
                    break;
                case CatFourCard.CAT_FOUR_CARD_ID:
                    amountCatCard4++;
                    break;
                case CatFiveCard.CAT_FIVE_CARD_ID:
                    amountCatCard5++;
                    break;
                case NopeCard.NOPE_CARD_ID:
                     amountNope++;
                     break;
                case SeeTheFutureCard.SEE_THE_FUTURE_CARD_ID:
                    amountFuture++;
                    break;
                case ShuffleCard.SHUFFLE_CARD_ID:
                    amountShuffle++;
                    break;
                case DefuseCard.DEFUSE_CARD_ID:
                    amountDefuse++;
                    break;
                case AttackCard.ATTACK_CARD_ID:
                    amountAttack++;
            }
        }
        Assertions.assertEquals(1,amountBomb);
        Assertions.assertEquals(4,amountAttack);
        Assertions.assertEquals(4,amountCatCard1);
        Assertions.assertEquals(4,amountCatCard2);
        Assertions.assertEquals(4,amountCatCard3);
        Assertions.assertEquals(4,amountCatCard4);
        Assertions.assertEquals(4,amountCatCard5);
        Assertions.assertEquals(6,amountDefuse);
        Assertions.assertEquals(4,amountFavour);
        Assertions.assertEquals(5,amountNope);
        Assertions.assertEquals(4,amountShuffle);
        Assertions.assertEquals(4,amountSkip);
        Assertions.assertEquals(5,amountFuture);
    }

}
