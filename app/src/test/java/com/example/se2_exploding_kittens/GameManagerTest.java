package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_BOMB_PULLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_CARD_PULLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.BombCard;
import com.example.se2_exploding_kittens.game_logic.cards.Card;
import com.example.se2_exploding_kittens.game_logic.cards.DefuseCard;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameManagerTest {

    NetworkManager networkManager;
    DiscardPile discardPile;
    Deck deck;
    CopyOnWriteArrayList<ServerTCPSocket> serverToClientConnections;

    @BeforeEach
    public void setupUpTest() {
        networkManager = mock(NetworkManager.class);
        serverToClientConnections = new CopyOnWriteArrayList<ServerTCPSocket>();
        when(networkManager.getServerConnections()).thenReturn(serverToClientConnections);
        when(networkManager.getConnectionRole()).thenReturn(TypeOfConnectionRole.SERVER);
        discardPile = new DiscardPile();
        deck = new Deck(1);
    }

    @Test
    void testReset() {
        GameManager gameManager = new GameManager(networkManager,deck,discardPile);
        gameManager.reset();
        verify(networkManager, times(5)).unsubscribeCallbackFromMessageID(anyObject(), anyInt());
        Assert.assertEquals(null, gameManager.getTurnManage());

        gameManager.reset();    // as networkManager is null unsubscribeCallbackFromMessageID gets no longer called
        verify(networkManager, times(5)).unsubscribeCallbackFromMessageID(anyObject(), anyInt());
    }

    // the following is an integration test

    void startGameSequence(GameManager gameManager, PlayerManager playerManager, int clientPlayers){
        playerManager.initializeAsHost(networkManager.getServerConnections(), networkManager);

        //4 players in total, one in local
        for(int i = 0; i < clientPlayers; i++){
            playerManager.clientConnected(null);
        }

        ArrayList<Player> players;
        players = new ArrayList<>();
        for (PlayerConnection pc : playerManager.getPlayers()) {
            players.add(pc.getPlayer());
            pc.getPlayer().subscribePlayerToCardEvents(networkManager);
        }

        deck.dealCards(players);
        gameManager = new GameManager(networkManager, deck, discardPile);
        gameManager.distributeDeck(deck);
        gameManager.distributePlayerHands();
        // player id 0 is always the host
        gameManager.startGame();
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void testStartGameStartSequence(int clientPlayers) {
        GameManager gameManager = new GameManager(networkManager,deck,discardPile);
        PlayerManager playerManager = PlayerManager.getInstance();
        startGameSequence(gameManager, playerManager, clientPlayers);

        Assert.assertEquals(clientPlayers+1, playerManager.getPlayerSize());
        int collectiveTurns = 0;
        //everyone gets 8 cards
        for (int i = 0; i<playerManager.getPlayerSize();i++){
            Assert.assertEquals(8, playerManager.getPlayer(i).getPlayer().getHand().size());
            boolean hasDefuse = false;
            for (int j = 0; j < playerManager.getPlayer(i).getPlayer().getHand().size(); j++){
                if(playerManager.getPlayer(i).getPlayer().getHand().get(j) instanceof DefuseCard){
                    hasDefuse = true;
                    break;
                }
            }
            Assert.assertTrue(hasDefuse);

            collectiveTurns = collectiveTurns + playerManager.getPlayer(i).getPlayer().getPlayerTurns();
        }

        //only one player has its turn
        Assert.assertEquals(1, collectiveTurns);
        int bomCount = 0;
        for (int i = 0; i < deck.getCards().size(); i++){
            if(deck.getCards().get(i) instanceof BombCard){
                bomCount++;
            }
        }
        Assert.assertEquals(bomCount, playerManager.getPlayerSize()-1);
    }

    @Test
    void testGAME_MANAGER_MESSAGE_CARD_PULLED_ID() {
        GameManager gameManager = new GameManager(networkManager,deck,discardPile);
        PlayerManager playerManager = PlayerManager.getInstance();
        //4 players in total
        startGameSequence(gameManager, playerManager, 3);
        Assert.assertEquals(4, playerManager.getPlayerSize());
        TurnManager turnManager = gameManager.getTurnManage();
        Card card = null;
        //lets pretend player 0 which is locally already lost since this method is called in response to client actions
        playerManager.getPlayer(0).getPlayer().setAlive(false);
        turnManager.gameStateNextTurn(1);
        int prevPlayer = 0;
        do{
            prevPlayer = turnManager.getCurrentPlayerID();
            card = deck.getCards().get(0);
            Message m;
            if(card instanceof BombCard){
                m = new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID, card.getCardID()+":"+turnManager.getCurrentPlayerID());
            }else{
                m = new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PULLED_ID, card.getCardID()+":"+turnManager.getCurrentPlayerID());
            }
            gameManager.responseReceived(m.getTransmitMessage(), null);
            if(!(card instanceof BombCard)){
                //TODO chage the check for the next player
/*                if(prevPlayer < 3){
                    Assert.assertEquals(((prevPlayer + 1) % playerManager.getPlayerSize()), turnManager.getCurrentPlayerID());
                }else {
                    Assert.assertEquals(1, turnManager.getCurrentPlayerID());
                }*/
                //check if the cards are on the hand
                ArrayList<Card> hand = playerManager.getPlayer(prevPlayer).getPlayer().getHand();
                Assert.assertEquals(card, hand.get(hand.size()-1));
                Assert.assertEquals(0, playerManager.getPlayer(prevPlayer).getPlayer().getPlayerTurns());
                Assert.assertEquals(1, playerManager.getPlayer(turnManager.getCurrentPlayerID()).getPlayer().getPlayerTurns());
            }else{
                Assert.assertTrue(discardPile.getCardPile().get(0) instanceof BombCard);
            }

        }while(!(card instanceof BombCard));
    }

    @Test
    void testGAME_MANAGER_MESSAGE_BOMB_PULLED_ID() {
        GameManager gameManager = new GameManager(networkManager,deck,discardPile);
        PlayerManager playerManager = PlayerManager.getInstance();
        //4 players in total
        startGameSequence(gameManager, playerManager, 3);
        Assert.assertEquals(4, playerManager.getPlayerSize());
        TurnManager turnManager = gameManager.getTurnManage();
        Card card = null;
        //lets pretend player 0 which is locally already lost since this method is called in response to client actions
        playerManager.getPlayer(0).getPlayer().setAlive(false);
        playerManager.getPlayer(0).getPlayer().setPlayerTurns(0);
        turnManager.gameStateNextTurn(1);
        int prevPlayer = 0;
        do{
            prevPlayer = turnManager.getCurrentPlayerID();
            card = deck.getCards().get(0);
            Message m;
            if(card instanceof BombCard){
                m = new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID, card.getCardID()+":"+turnManager.getCurrentPlayerID());
            }else{
                m = new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PULLED_ID, card.getCardID()+":"+turnManager.getCurrentPlayerID());
            }
            gameManager.responseReceived(m.getTransmitMessage(), null);
            if((card instanceof BombCard)){
                Assert.assertTrue(discardPile.getCardPile().get(0) instanceof BombCard);
                Assert.assertTrue(playerManager.getPlayer(prevPlayer).getPlayer().isHasBomb());
                int collectiveTurns = 0;
                //everyone gets 8 cards
                for (int i = 0; i<playerManager.getPlayerSize();i++){
                    collectiveTurns = collectiveTurns + playerManager.getPlayer(i).getPlayer().getPlayerTurns();
                }
                //no player has its turn, only one has the bomb
                Assert.assertEquals(0, collectiveTurns);
            }else {
                Assert.assertEquals(0, playerManager.getPlayer(prevPlayer).getPlayer().getPlayerTurns());
                Assert.assertEquals(1, playerManager.getPlayer(turnManager.getCurrentPlayerID()).getPlayer().getPlayerTurns());
            }

        }while(!(card instanceof BombCard));
        //check if the implemented cards are on the pile unimplemented cards don`t end up there
    }

    @Test
    void testGAME_MANAGER_MESSAGE_NOPE_ENABLED_ID() {
        GameManager gameManager = new GameManager(networkManager,deck,discardPile);
        PlayerManager playerManager = PlayerManager.getInstance();
        //4 players in total
        startGameSequence(gameManager, playerManager, 3);
        Assert.assertEquals(4, playerManager.getPlayerSize());

        Message m = new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID, "");
        gameManager.responseReceived(m.getTransmitMessage(), null);
        Assert.assertTrue(GameLogic.nopeEnabled);
    }
    @Test
    void testGAME_MANAGER_MESSAGE_NOPE_DISABLED_ID() {
        GameManager gameManager = new GameManager(networkManager,deck,discardPile);
        PlayerManager playerManager = PlayerManager.getInstance();
        //4 players in total
        startGameSequence(gameManager, playerManager, 3);
        Assert.assertEquals(4, playerManager.getPlayerSize());

        Message m = new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID, "");
        gameManager.responseReceived(m.getTransmitMessage(), null);
        Assert.assertFalse(GameLogic.nopeEnabled);
    }





}
