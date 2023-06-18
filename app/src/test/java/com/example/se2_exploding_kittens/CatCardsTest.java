package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.game_logic.cards.CatFiveCard.CAT_FIVE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.CatFourCard.CAT_FOUR_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.CatOneCard.CAT_ONE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.CatThreeCard.CAT_THREE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.CatTwoCard.CAT_TWO_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.DefuseCard.DEFUSE_CARD_ID;
import static org.mockito.Mockito.mock;

import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.CatFiveCard;
import com.example.se2_exploding_kittens.game_logic.cards.CatFourCard;
import com.example.se2_exploding_kittens.game_logic.cards.CatOneCard;
import com.example.se2_exploding_kittens.game_logic.cards.CatThreeCard;
import com.example.se2_exploding_kittens.game_logic.cards.CatTwoCard;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CatCardsTest {
    NetworkManager networkManager;
    DiscardPile discardPile;


    @BeforeEach
    public void setupUpTest() {
        networkManager = mock(NetworkManager.class);
        discardPile = new DiscardPile();
    }

    @Test
    void testCatCardHandleActions() {
        Player player = new Player();
        player.setPlayerTurns(1);
        player.setAlive(true);
        player.setPlayerId(1);
        player.addCardToHand(String.valueOf(CAT_ONE_CARD_ID));
        player.addCardToHand(String.valueOf(CAT_TWO_CARD_ID));
        player.addCardToHand(String.valueOf(CAT_THREE_CARD_ID));
        player.addCardToHand(String.valueOf(CAT_FOUR_CARD_ID));
        player.addCardToHand(String.valueOf(CAT_FIVE_CARD_ID));
        player.addCardToHand(String.valueOf(DEFUSE_CARD_ID));

        NetworkManager networkManager = mock(NetworkManager.class);

        CatOneCard coc = new CatOneCard();
        coc.handleActions(player, networkManager, discardPile, null);

        //Test counter for CatOne = 1
        Assertions.assertEquals(1, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof CatTwoCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof CatOneCard);
        Assertions.assertEquals(1, player.getCatOneCounter());

        CatTwoCard ctc = new CatTwoCard();
        ctc.handleActions(player, networkManager, discardPile, null);

        //Test counter for CatTwo = 1, and CatOne is still 1
        Assertions.assertEquals(1, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof CatThreeCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof CatTwoCard);
        Assertions.assertEquals(1, player.getCatTwoCounter());
        Assertions.assertEquals(1, player.getCatOneCounter());

        CatThreeCard cthc = new CatThreeCard();
        cthc.handleActions(player, networkManager, discardPile, null);

        //Test counter for CatThree = 1, and CatOne and CatTwo are still 1
        Assertions.assertEquals(1, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof CatFourCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof CatThreeCard);
        Assertions.assertEquals(1, player.getCatThreeCounter());
        Assertions.assertEquals(1, player.getCatTwoCounter());
        Assertions.assertEquals(1, player.getCatOneCounter());

        CatFourCard cfc = new CatFourCard();
        cfc.handleActions(player, networkManager, discardPile, null);

        //Test counter for CatFour = 1, and CatOne, CatTwo, CatThree are still 1
        Assertions.assertEquals(1, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof CatFiveCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof CatFourCard);
        Assertions.assertEquals(1, player.getCatFourCounter());
        Assertions.assertEquals(1, player.getCatThreeCounter());
        Assertions.assertEquals(1, player.getCatTwoCounter());
        Assertions.assertEquals(1, player.getCatOneCounter());

        CatFiveCard cfic = new CatFiveCard();
        cfic.handleActions(player, networkManager, discardPile, null);

        //Test counter for CatFive = 1, and CatOne, CatTwo, CatThree and CatFour are still 1
        Assertions.assertEquals(1, player.getPlayerTurns());
        Assertions.assertTrue(player.getHand().get(0) instanceof DefuseCard);
        Assertions.assertTrue(discardPile.getCardPile().get(0) instanceof CatFiveCard);
        Assertions.assertEquals(1, player.getCatFiveCounter());
        Assertions.assertEquals(1, player.getCatFourCounter());
        Assertions.assertEquals(1, player.getCatThreeCounter());
        Assertions.assertEquals(1, player.getCatTwoCounter());
        Assertions.assertEquals(1, player.getCatOneCounter());

        player.setPlayerTurns(0);
        //Test if when pulling a card resets the counter
        Assertions.assertEquals(0, player.getCatFiveCounter());
        Assertions.assertEquals(0, player.getCatFourCounter());
        Assertions.assertEquals(0, player.getCatThreeCounter());
        Assertions.assertEquals(0, player.getCatTwoCounter());
        Assertions.assertEquals(0, player.getCatOneCounter());

        //Test if cat counter has a certain amount works
        Assertions.assertFalse(player.isCatCounter(coc, 1));
        Assertions.assertFalse(player.isCatCounter(ctc, 2));
        Assertions.assertFalse(player.isCatCounter(cthc, 3));
        Assertions.assertFalse(player.isCatCounter(cfc, 4));
        Assertions.assertTrue(player.isCatCounter(cfic, 0));
    }
}
