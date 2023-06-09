package com.example.se2_exploding_kittens.game_logic;

import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_BOMB_PULLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_CARD_INSERTED_TO_DECK_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_CARD_PLAYED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_CARD_PULLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_SEND_CARD_TO_PLAYER_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_UPDATE_PLAYER_HAND_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_PLAYER_LOST_ID;
import static com.example.se2_exploding_kittens.Network.GameManager.GAME_MANAGER_MESSAGE_PLAYER_WON_ID;
import static com.example.se2_exploding_kittens.Network.PlayerManager.LOCAL_PLAYER_MANAGER_ID_ASSIGNED;
import static com.example.se2_exploding_kittens.Network.PlayerManager.LOCAL_PLAYER_MANAGER_ID_PLAYER_DISCONNECT;
import static com.example.se2_exploding_kittens.Network.PlayerManager.PLAYER_MANAGER_MESSAGE_ID;
import static com.example.se2_exploding_kittens.Network.PlayerManager.LOCAL_PLAYER_MANAGER_MESSAGE_PLAYER_IDS_ID;
import static com.example.se2_exploding_kittens.TurnManager.LOCAL_TURN_MANAGER_ASSIGN_TURNS;
import static com.example.se2_exploding_kittens.TurnManager.TURN_MANAGER_MESSAGE_ID;
import static com.example.se2_exploding_kittens.TurnManager.LOCAL_TURN_MANAGER_TURN_FINISHED;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.se2_exploding_kittens.Activities.GameActivity;
import com.example.se2_exploding_kittens.Network.DisconnectedCallback;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ClientTCP;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.game_logic.cards.Card;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GameClient implements MessageCallback, DisconnectedCallback {

    private Player player;
    private Deck deck;
    private DiscardPile discardPile;
    private NetworkManager networkManager;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private PropertyChangeListener handInitializedChangeListener = evt -> {
        if ("handInitialized".equals(evt.getPropertyName())) {
            checkIfGameInitialized();
        }
    };

    public GameClient(Player player, Deck deck, DiscardPile discardPile, NetworkManager networkManager) {
        this.player = player;
        this.player.addPropertyChangeListener(handInitializedChangeListener);
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
        this.networkManager.subscribeCallbackToMessageID(this, LOCAL_PLAYER_MANAGER_MESSAGE_PLAYER_IDS_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_UPDATE_PLAYER_HAND_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_SEND_CARD_TO_PLAYER_ID);
    }




    public Player getPlayer() {
        return this.player;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
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

    public void checkIfGameInitialized(){
        if(player.getPlayerId() != -1 && deck != null && player.getHand().size() > 0){
            propertyChangeSupport.firePropertyChange("playerInitialized",null,player);
        }
    }

    public void blockUntilReady(Activity activity) {
        double timeout = 60000; // 1minute timeout
        while (player.getPlayerId() == -1 && timeout > 0) {
            try {
                Thread.sleep(20);
                timeout-=20;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        while (deck == null && timeout > 0) {
            try {
                Thread.sleep(20);
                timeout-=20;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        while (player.getHand().size() == 0 && timeout > 0) {
            try {
                Thread.sleep(20);
                timeout-=20;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if(timeout <= 0){
            activity.finish();
        }
    }

    @Override
    public void connectionDisconnected(Object connection) {
        networkManager.terminateConnection();
    }

    public void sendTurnFinished() {
        String gameStateMessage = LOCAL_TURN_MANAGER_TURN_FINISHED + ":" + player.getPlayerId();
        try {
            Message m = new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage);
            networkManager.sendMessageFromTheClient(m);
        } catch (IllegalAccessException e) {
            //this was called without proper network initialisation
        }
    }

    public void sendCardPulled() {
        String gameStateMessage = LOCAL_TURN_MANAGER_TURN_FINISHED + ":" + player.getPlayerId();
        try {
            Message m = new Message(MessageType.MESSAGE, TURN_MANAGER_MESSAGE_ID, gameStateMessage);
            networkManager.sendMessageFromTheClient(m);
        } catch (IllegalAccessException e) {
            //this was called without proper network initialisation
        }
    }

    private void handleTurnManagerMessage(int messageType, int turns, int playerID) {
        switch (messageType) {
            case LOCAL_TURN_MANAGER_TURN_FINISHED:
                if (playerID == player.getPlayerId()) {
                    player.setPlayerTurns(0);
                }
                break;
            case LOCAL_TURN_MANAGER_ASSIGN_TURNS:
                if (player.getPlayerId() == playerID) {
                    player.setPlayerTurns(turns);
                }else{
                    player.setPlayerTurns(0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {

        if (text != null) {

            handlePlayerManagerMessages(text);

            handlePlayerLostFromGameManagerMessage(text, sender);

            handlePlayerWonFromGameManagerMessage(text, sender);

            handleTurnManagerMessage(text, sender);

            handleCardPulledFromGameManagerMessage(text);

            handleBombPulledFromGameManagerMessage(text);

            handleCardPlayedFromGameManagerMessage(text);

            handleCardInsertedFromGameManagerMessage(text);
            handleSendCardToPlayerMessage(text);

            if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID) {
                GameLogic.nopeEnabled = true;
            }
            if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID) {
                GameLogic.nopeEnabled = false;
            }
            if (Message.parseAndExtractMessageID(text) == LOCAL_PLAYER_MANAGER_MESSAGE_PLAYER_IDS_ID) {
                String playerIDs = Message.parseAndExtractPayload(text);
                GameLogic.setPlayerIDList(playerIDs);
            }
        }

    }

    private void handleCardInsertedFromGameManagerMessage(String text) {
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_INSERTED_TO_DECK_ID) {
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2) {
                int cardID = Integer.parseInt(message[0]);
                int idx = Integer.parseInt(message[1]);
                deck.insertCard(cardID, idx);
            }
        }
    }

    private void handleSendCardToPlayerMessage(String text){
        if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_SEND_CARD_TO_PLAYER_ID){
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2){
                int playerID = Integer.parseInt(message[0]);
                if(playerID == player.getPlayerId()){
                    GameLogic.addCardToHand(player, networkManager, message[1]);
                }
            }
        }
    }

    private void handleCardPlayedFromGameManagerMessage(String text) {
        if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PLAYED_ID){
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2){
                Log.v("GameClient", "Cardplayed"+message[0]+" player "+message[1]);
                int playerID = Integer.parseInt(message[1]);
                if(playerID != player.getPlayerId()){
                    GameLogic.cardHasBeenPlayed(null,Deck.getCardByID(Integer.parseInt(message[0])),networkManager,discardPile,null, deck, null);
                }
            }
        }
    }

        private void handleBombPulledFromGameManagerMessage (String text){
            if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_BOMB_PULLED_ID) {
                String[] message = Message.parseAndExtractPayload(text).split(":");
                if (message.length == 2) {
                    int playerID = Integer.parseInt(message[1]);
                    if (playerID != player.getPlayerId()) {
                        Card removedCard = deck.removeCard(Integer.parseInt(message[0]));
                        discardPile.putCard(removedCard);
                    }
                }
            }
        }

    private void handleCardPulledFromGameManagerMessage(String text) {
        if(Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PULLED_ID){
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2){
                int playerID = Integer.parseInt(message[1]);
                if(playerID != player.getPlayerId()){
                    deck.removeCard(Integer.parseInt(message[0]));
                }
            }
        }
    }

        private void handleTurnManagerMessage (String text, Object sender){
            if (Message.parseAndExtractMessageID(text) == TURN_MANAGER_MESSAGE_ID && sender instanceof ClientTCP) {
                String[] message = Message.parseAndExtractPayload(text).split(":");
                if (message.length >= 3) {
                    handleTurnManagerMessage(Integer.parseInt(message[0]), Integer.parseInt(message[1]), Integer.parseInt(message[2]));
                }
            }
        }

        private void handlePlayerWonFromGameManagerMessage (String text, Object sender){
            if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_PLAYER_WON_ID && sender instanceof ClientTCP) {
                String message = Message.parseAndExtractPayload(text);
                if (Integer.parseInt(message) == player.getPlayerId()) {
                    //player lost
                    networkManager.terminateConnection();
                    player.setHasWon(true);
                }
            }
        }

        private void handlePlayerLostFromGameManagerMessage (String text, Object sender){
            if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_PLAYER_LOST_ID && sender instanceof ClientTCP) {
                String message = Message.parseAndExtractPayload(text);
                if (Integer.parseInt(message) == player.getPlayerId()) {
                    //player lost
                    networkManager.terminateConnection();
                    player.setAlive(false);
                }
            }
        }

    private void handlePlayerManagerMessages(String text) {
        if(PLAYER_MANAGER_MESSAGE_ID == Message.parseAndExtractMessageID(text)){
            int playerID = -1;
            switch (PlayerManager.parseTypeFromPayload(Message.parseAndExtractPayload(text))){
                case LOCAL_PLAYER_MANAGER_ID_ASSIGNED:
                    playerID = Integer.parseInt(PlayerManager.parseDataFromPayload(Message.parseAndExtractPayload(text)));
                    if(playerID != -1){
                        player.setPlayerId(playerID);
                        checkIfGameInitialized();
                    }
                    break;
                case LOCAL_PLAYER_MANAGER_ID_PLAYER_DISCONNECT:
                    playerID = Integer.parseInt(PlayerManager.parseDataFromPayload(Message.parseAndExtractPayload(text)));
                    if(playerID == player.getPlayerId()){
                        networkManager.terminateConnection();
                    }
                    break;
            }
        }
    }
}
