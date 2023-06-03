package com.example.se2_exploding_kittens.Network;

import static com.example.se2_exploding_kittens.game_logic.PlayerMessageID.PLAYER_HAND_MESSAGE_ID;

import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.Card;
import com.example.se2_exploding_kittens.game_logic.cards.NopeCard;

import java.util.ArrayList;

public class GameManager implements MessageCallback {

    private NetworkManager networkManager;
    private TurnManager turnManager;
    private int numberOfPlayers;
    private Deck deck;
    private PlayerManager playerManager;
    private DiscardPile discardPile;
    boolean nopeEnabled = false;
    public static final int GAME_MANAGER_MESSAGE_ID = 500;
    public static final int GAME_MANAGER_MESSAGE_CARD_PULLED_ID = 501;
    public static final int GAME_MANAGER_MESSAGE_CARD_REMOVED = 502;
    public static final int GAME_MANAGER_MESSAGE_CARD_PLAYED_ID = 503;
    public static final int GAME_MANAGER_MESSAGE_BOMB_PULLED_ID = 504;

    public GameManager(NetworkManager networkManager, Deck deck, DiscardPile discardPile) {
        this.networkManager = networkManager;
        this.playerManager = PlayerManager.getInstance();
        this.turnManager = new TurnManager(networkManager);
        this.deck = deck;
        this.discardPile = discardPile;
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_CARD_PULLED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID);
        this.numberOfPlayers = turnManager.getNumberOfPlayers();
    }

    public void updateDeck(Deck deck){
        this.deck = deck;
    }

    public void startGame(){
        turnManager.startGame();
    }

    public void distributePlayerHands() {
        try {
            if(playerManager != null && networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER){
                for (PlayerConnection p: playerManager.getPlayers()) {
                    if(p.getConnection() != null){
                        networkManager.sendMessageFromTheSever(new Message(MessageType.MESSAGE, PLAYER_HAND_MESSAGE_ID.id, p.getPlayer().getPlayerId()+":"+p.getPlayer().handToString()),p.getConnection());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendCardPulled(int playerID, Card card, NetworkManager networkManager) {
        try {
            if(networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER){
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID, card.getCardID()+":"+playerID));

            }else if(networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT){
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID, card.getCardID()+":"+playerID));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendBombPulled(int playerID, Card card, NetworkManager networkManager) {
        try {
            if(networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER){
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID, card.getCardID()+":"+playerID));

            }else if(networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT){
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID, card.getCardID()+":"+playerID));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendCardPlayed(int playerID, Card card, NetworkManager networkManager) {
        try {
            if(networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER){
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PULLED_ID, card.getCardID()+":"+playerID));

            }else if(networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT){
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PULLED_ID, card.getCardID()+":"+playerID));
            }
            // player has to draw another card
            /*if (playerManager.getPlayer(playerID).numberOfTurnsLeft() <= 0) {
                turnManager.broadcastTurnFinished();
                turnManager.sendNextGameSateToPlayers();
            }*/
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {
        if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PULLED_ID){
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2){
                int playerID = Integer.parseInt(message[1]);
                if(playerID != playerManager.getLocalSelf().getPlayerId()){
                    Card removedCard = deck.removeCard(Integer.parseInt(message[0]));
                    if(networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER){
                        //broadcast to other clients
                        sendCardPulled(playerID,removedCard, networkManager);
                        playerManager.getPlayer(playerID).getPlayer().addCardToHand(Integer.toString(removedCard.getCardID()));
                    }
                }
            }
        }
        if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_BOMB_PULLED_ID){
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2){
                int playerID = Integer.parseInt(message[1]);
                if(playerID != playerManager.getLocalSelf().getPlayerId()){
                    Card removedCard = deck.removeCard(Integer.parseInt(message[0]));
                    if(networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER){
                        //broadcast to other clients
                        sendCardPulled(playerID,removedCard, networkManager);
                        playerManager.getPlayer(playerID).getPlayer().setHasBomb(true);
                        discardPile.putCard(removedCard);
                    }
                }
            }
        }
        if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PLAYED_ID){
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2){
                int playerID = Integer.parseInt(message[1]);
                if(playerID != playerManager.getLocalSelf().getPlayerId()){
                    Card playedCard = discardPile.putCard(Integer.parseInt(message[0]));

                    if(networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER){
                        //broadcast to other clients
                        sendCardPlayed(playerID,playedCard, networkManager);
                        playerManager.getPlayer(playerID).getPlayer().removeCardFromHand(Integer.toString(playedCard.getCardID()));
                        discardPile.putCard(playedCard);
                    }
                }
            }
        }
    }
}
