package com.example.se2_exploding_kittens.game_logic;

import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_BOMB_PULLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_CARD_INSERTED_TO_DECK_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_CARD_PLAYED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_CARD_PULLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_PLAYER_LOST_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_PLAYER_WON_ID;
import static com.example.se2_exploding_kittens.Network.PlayerManager.PLAYER_MANAGER_ID_ASSIGNED;
import static com.example.se2_exploding_kittens.Network.PlayerManager.PLAYER_MANAGER_ID_PLAYER_DISCONNECT;
import static com.example.se2_exploding_kittens.Network.PlayerManager.PLAYER_MANAGER_MESSAGE_ID;
import static com.example.se2_exploding_kittens.TurnManager.TURN_MANAGER_ASSIGN_TURNS;
import static com.example.se2_exploding_kittens.TurnManager.TURN_MANAGER_MESSAGE_ID;
import static com.example.se2_exploding_kittens.TurnManager.TURN_MANAGER_TURN_FINISHED;

import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

import com.example.se2_exploding_kittens.GameActivity;
import com.example.se2_exploding_kittens.JoinGameActivity;
import com.example.se2_exploding_kittens.Network.DisconnectedCallback;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ClientTCP;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.game_logic.cards.Card;

public class GameClient implements MessageCallback, DisconnectedCallback {

    private Player player;
    private Deck deck;
    private DiscardPile discardPile;
    private NetworkManager networkManager;

    public GameClient(Player player, Deck deck, DiscardPile discardPile, NetworkManager networkManager){
        this.player = player;
        this.deck = deck;
        this.discardPile = discardPile;
        this.networkManager = networkManager;
        this.networkManager.subscribeToDisconnectedCallback(this);
        this.networkManager.subscribeCallbackToMessageID(this, PLAYER_MANAGER_MESSAGE_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_CARD_PULLED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, TURN_MANAGER_MESSAGE_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_CARD_INSERTED_TO_DECK_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_PLAYER_LOST_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_PLAYER_WON_ID);

    }

    public Player getPlayer() {
        return this.player;
    }

    public Deck getDeck() {
        return this.deck;
    }
    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public DiscardPile getDiscardPile() {
        return this.discardPile;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public void blockUntilReady(){
        while (player.getPlayerId() == -1){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        while (deck == null){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        while (player.getHand().size() == 0){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void connectionDisconnected(Object connection) {
        networkManager.terminateConnection();
    }

    public void sendTurnFinished(){
        String gameStateMessage = TURN_MANAGER_TURN_FINISHED +":"+ player.getPlayerId();
        try {
            Message m = new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage);
            networkManager.sendMessageFromTheClient(m);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void sendCardPulled(){
        String gameStateMessage = TURN_MANAGER_TURN_FINISHED +":"+ player.getPlayerId();
        try {
            Message m = new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage);
            networkManager.sendMessageFromTheClient(m);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void handleTurnManagerMessage(int messageType, int turns, int playerID){
        switch (messageType){
            case TURN_MANAGER_TURN_FINISHED:
                if(playerID == player.getPlayerId()){
                    player.setPlayerTurns(0);
                }
                break;
            case TURN_MANAGER_ASSIGN_TURNS:
                if(player.getPlayerId() == playerID){
                    player.setPlayerTurns(turns);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {

        if(text !=  null){
            if(PLAYER_MANAGER_MESSAGE_ID == Message.parseAndExtractMessageID(text)){
                int playerID = -1;
                switch (PlayerManager.parseTypeFromPayload(Message.parseAndExtractPayload(text))){
                    case PLAYER_MANAGER_ID_ASSIGNED:
                        playerID = Integer.parseInt(PlayerManager.parseDataFromPayload(Message.parseAndExtractPayload(text)));
                        if(playerID != -1){
                            player.setPlayerId(playerID);
                        }
                        break;
                    case PLAYER_MANAGER_ID_PLAYER_DISCONNECT:
                        playerID = Integer.parseInt(PlayerManager.parseDataFromPayload(Message.parseAndExtractPayload(text)));
                        if(playerID == player.getPlayerId()){
                            networkManager.terminateConnection();
                        }
                        break;
                }
            }

            if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_PLAYER_LOST_ID){
                if(sender instanceof ClientTCP){
                    String  message = Message.parseAndExtractPayload(text);
                    if(Integer.parseInt(message) == player.getPlayerId()){
                        //player lost
                        networkManager.terminateConnection();
                        player.setAlive(false);
                    }
                }
            }

            if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_PLAYER_WON_ID){
                if(sender instanceof ClientTCP){
                    String  message = Message.parseAndExtractPayload(text);
                    if(Integer.parseInt(message) == player.getPlayerId()){
                        //player lost
                        networkManager.terminateConnection();
                        player.setHasWon(true);
                    }
                }
            }

            if(Message.parseAndExtractMessageID(text) == TURN_MANAGER_MESSAGE_ID){
                if(sender instanceof ClientTCP){
                    String [] message = Message.parseAndExtractPayload(text).split(":");
                    if(message.length >=3){
                        handleTurnManagerMessage(Integer.parseInt(message[0]), Integer.parseInt(message[1]), Integer.parseInt(message[2]));
                    }
                }
            }

            if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PULLED_ID){
                String[] message = Message.parseAndExtractPayload(text).split(":");
                if (message.length == 2){
                    int playerID = Integer.parseInt(message[1]);
                    if(playerID != player.getPlayerId()){
                        deck.removeCard(Integer.parseInt(message[0]));
                    }
                }
            }
            if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_BOMB_PULLED_ID){
                String[] message = Message.parseAndExtractPayload(text).split(":");
                if (message.length == 2){
                    int playerID = Integer.parseInt(message[1]);
                    if(playerID != player.getPlayerId()){
                        Card removedCard = deck.removeCard(Integer.parseInt(message[0]));
                        discardPile.putCard(removedCard);
                    }
                }
            }
            if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PLAYED_ID){
                String[] message = Message.parseAndExtractPayload(text).split(":");
                if (message.length == 2){
                    int playerID = Integer.parseInt(message[1]);
                    if(playerID != player.getPlayerId()){
                        GameLogic.cardHasBeenPlayed(null,Deck.getCardByID(Integer.parseInt(message[0])),networkManager,discardPile,null, deck, null);
                    }
                }
            }
            if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID){
                GameLogic.nopeEnabled = true;
            }
            if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID){
                GameLogic.nopeEnabled = false;
            }
            if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_INSERTED_TO_DECK_ID){
                String[] message = Message.parseAndExtractPayload(text).split(":");
                if (message.length == 2){
                    int cardID = Integer.parseInt(message[0]);
                    int idx = Integer.parseInt(message[1]);
                    deck.insertCard(cardID,idx);
                }
            }
        }
    }
}
