package com.example.se2_exploding_kittens.Network;

import static com.example.se2_exploding_kittens.Activities.GameActivity.GAME_ACTIVITY_DECK_MESSAGE_ID;
import static com.example.se2_exploding_kittens.Activities.GameActivity.GAME_ACTIVITY_SHOW_THREE_CARDS_ID;
import static com.example.se2_exploding_kittens.Network.PlayerManager.LOCAL_PLAYER_MANAGER_MESSAGE_PLAYER_IDS_ID;
import static com.example.se2_exploding_kittens.game_logic.PlayerMessageID.PLAYER_HAND_MESSAGE_ID;
import static com.example.se2_exploding_kittens.Activities.GameActivity.GAME_ACTIVITY_STEAL_CARD;

import android.util.Log;

import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.PlayerMessageID;
import com.example.se2_exploding_kittens.game_logic.cards.Card;

public class GameManager implements MessageCallback {

    private NetworkManager networkManager;
    private TurnManager turnManager;
    private Deck deck;
    private PlayerManager playerManager;
    private DiscardPile discardPile;
    public static final int GAME_MANAGER_MESSAGE_CARD_PULLED_ID = 501;
    public static final int GAME_MANAGER_MESSAGE_CARD_PLAYED_ID = 503;
    public static final int GAME_MANAGER_MESSAGE_BOMB_PULLED_ID = 504;
    public static final int GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID = 506;
    public static final int GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID = 507;
    public static final int GAME_MANAGER_MESSAGE_PLAYER_LOST_ID = 508;
    public static final int GAME_MANAGER_MESSAGE_CARD_INSERTED_TO_DECK_ID = 509;
    public static final int GAME_MANAGER_MESSAGE_PLAYER_WON_ID = 510;
    public static final int GAME_MANAGER_MESSAGE_CHECKED_CARD = 511;
    public static final int GAME_MANAGER_MESSAGE_CHECKED_CARD = 508;
    public static final int GAME_MANAGER_MESSAGE_DISCARD_PILE_PULLED_ID = 502;

    public GameManager(NetworkManager networkManager, Deck deck, DiscardPile discardPile) {
        this.networkManager = networkManager;
        this.playerManager = PlayerManager.getInstance();
        this.turnManager = new TurnManager(networkManager);
        this.deck = deck;
        this.discardPile = discardPile;
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_CARD_PULLED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_ACTIVITY_STEAL_CARD);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_ACTIVITY_SHOW_THREE_CARDS_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_ACTIVITY_DECK_MESSAGE_ID);
        this.networkManager.subscribeCallbackToMessageID(this, LOCAL_PLAYER_MANAGER_MESSAGE_PLAYER_IDS_ID);
        this.networkManager.subscribeCallbackToMessageID(this, GAME_MANAGER_MESSAGE_DISCARD_PILE_PULLED_ID);
    }

    public void reset(){
        if(networkManager != null){
            this.networkManager.unsubscribeCallbackFromMessageID(this, GAME_MANAGER_MESSAGE_CARD_PULLED_ID);
            this.networkManager.unsubscribeCallbackFromMessageID(this, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID);
            this.networkManager.unsubscribeCallbackFromMessageID(this, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID);
            this.networkManager.unsubscribeCallbackFromMessageID(this, GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID);
            this.networkManager.unsubscribeCallbackFromMessageID(this, GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID);
            this.networkManager = null;
        }
        this.playerManager = null;
        this.turnManager = null;
        this.deck = null;
        this.discardPile = null;
    }

    public TurnManager getTurnManage() {
        return turnManager;
    }

    public void startGame() {
        turnManager.startGame();
    }

    public void distributePlayerHands() {
        try {
            if (playerManager != null && NetworkManager.isServer(networkManager)) {
                for (PlayerConnection p : playerManager.getPlayers()) {
                    if (p.getConnection() != null) {
                        networkManager.sendMessageFromTheSever(new Message(MessageType.MESSAGE, PLAYER_HAND_MESSAGE_ID.id, p.getPlayer().getPlayerId() + ":" + p.getPlayer().handToString()), p.getConnection());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void checkGameEnd() {
        int winner = GameLogic.checkForWinner(playerManager);
        if (winner != -1) {
            playerManager.getPlayer(winner).getPlayer().setHasWon(true);
            if (winner != 0) {
                sendPlayerWon(winner, networkManager);
            }
        }
    }

    public static void sendDeckInsetCard(NetworkManager networkManager, int cardID, int idx) {
        try {
            networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_INSERTED_TO_DECK_ID, cardID + ":" + idx));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendNopeEnabled(NetworkManager networkManager) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                GameLogic.nopeEnabled = true;
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID, ""));
            } else if (NetworkManager.isClient(networkManager)) {
                GameLogic.nopeEnabled = true;
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID, ""));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendNopeDisabled(NetworkManager networkManager) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                GameLogic.nopeEnabled = false;
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID, ""));
            } else if (NetworkManager.isClient(networkManager)) {
                GameLogic.nopeEnabled = false;
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID, ""));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendCardPulled(int playerID, Card card, NetworkManager networkManager) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PULLED_ID, card.getCardID() + ":" + playerID));

            } else if (NetworkManager.isClient(networkManager)) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PULLED_ID, card.getCardID() + ":" + playerID));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendPlayerWon(int playerID, NetworkManager networkManager) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_PLAYER_WON_ID, Integer.toString(playerID)));

            } else if (NetworkManager.isClient(networkManager)) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_PLAYER_WON_ID, Integer.toString(playerID)));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendPlayerLost(int playerID, NetworkManager networkManager) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_PLAYER_LOST_ID, Integer.toString(playerID)));

            } else if (NetworkManager.isClient(networkManager)) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_PLAYER_LOST_ID, Integer.toString(playerID)));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendBombPulled(int playerID, Card card, NetworkManager networkManager) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID, card.getCardID() + ":" + playerID));

            } else if (NetworkManager.isClient(networkManager)) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID, card.getCardID() + ":" + playerID));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendCardPlayed(int playerID, Card card, NetworkManager networkManager) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID, card.getCardID() + ":" + playerID));

            } else if (NetworkManager.isClient(networkManager)) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID, card.getCardID() + ":" + playerID));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void distributeDeck(NetworkManager networkManager, Deck deck) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_ACTIVITY_DECK_MESSAGE_ID, deck.deckToString()));
            } else if (NetworkManager.isClient(networkManager)) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_ACTIVITY_DECK_MESSAGE_ID, deck.deckToString()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void showTopThreeCards(int playerID, NetworkManager networkManager) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_ACTIVITY_SHOW_THREE_CARDS_ID, Integer.toString(playerID)));
            } else if (NetworkManager.isClient(networkManager)) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_ACTIVITY_SHOW_THREE_CARDS_ID, Integer.toString(playerID)));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendGiveAwayCard(String fromPlayerIDToPlayerID, NetworkManager networkManager) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_ACTIVITY_FAVOR_CARD_ID, fromPlayerIDToPlayerID));
            } else if (NetworkManager.isClient(networkManager)) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_ACTIVITY_FAVOR_CARD_ID, fromPlayerIDToPlayerID));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendCardTo(int playerID, NetworkManager networkManager, Card card) {
        try {
            if (NetworkManager.isServer(networkManager)) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, PlayerMessageID.PLAYER_CARD_ADDED_MESSAGE_ID.id, playerID + ":" + card.getCardID()));
            } else if (NetworkManager.isClient(networkManager)) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, PlayerMessageID.PLAYER_CARD_ADDED_MESSAGE_ID.id, playerID + ":" + card.getCardID()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {
        handleCardPulledMessage(text);
        handleBombPulledMessage(text);
        handleCardPlayedMessage(text);
        handleNopeEnabledMessage(text);
        handleNopeDisabledMessage(text);
        handleDistributeDeckMessage(text);
        handleFavorCardMessage(text);
        handleShowThreeCardsMessage(text);
        handleRecieveCard(text);
    }

    private void handleNopeDisabledMessage(String text) {
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID) {
            GameLogic.nopeEnabled = false;
            if (NetworkManager.isServer(networkManager)) {
                //broadcast to other clients
                sendNopeDisabled(networkManager);
            }
        }
    }

    private void handleNopeEnabledMessage(String text) {
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID) {
            GameLogic.nopeEnabled = true;
            if (NetworkManager.isServer(networkManager)) {
                //broadcast to other clients
                sendNopeEnabled(networkManager);
            }
        }
    }

    private void handleCardPlayedMessage(String text) {
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PLAYED_ID) {
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2) {
                int playerID = Integer.parseInt(message[1]);
                if (playerID != playerManager.getLocalSelf().getPlayerId()) {

                    Card playedCard = Deck.getCardByID(Integer.parseInt(message[0]));

                    if (NetworkManager.isServer(networkManager)) {
                        //broadcast to other clients
                        sendCardPlayed(playerID, playedCard, networkManager);
                        GameLogic.cardHasBeenPlayed(playerManager.getPlayer(playerID).getPlayer(), playedCard, networkManager, discardPile, turnManager, deck, null);
                    }
                }
            }
        }
    }

    private void handleBombPulledMessage(String text) {
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_BOMB_PULLED_ID) {
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2) {
                int playerID = Integer.parseInt(message[1]);
                //Log.v("GameManager", "Bombpulled player "+message[1]);
                if (playerID != playerManager.getLocalSelf().getPlayerId()) {
                    Card removedCard = deck.removeCard(Integer.parseInt(message[0]));
                    if (NetworkManager.isServer(networkManager)) {
                        //broadcast to other clients
                        //sendBombPulled(playerID, removedCard, networkManager);
                        GameLogic.cardHasBeenPulled(playerManager.getPlayer(playerID).getPlayer(), removedCard, networkManager, discardPile, turnManager);
                        checkGameEnd();
                    }
                }
            }
        }
    }

    private void handleCardPulledMessage(String text) {
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PULLED_ID) {
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2) {
                //Log.v("GameManager", "Cardpulled"+message[0]+" player "+message[1]);
                int playerID = Integer.parseInt(message[1]);
                if (playerID != playerManager.getLocalSelf().getPlayerId()) {
                    Card removedCard = deck.removeCard(Integer.parseInt(message[0]));
                    if (NetworkManager.isServer(networkManager)) {
                        //broadcast to other clients
                        //sendCardPulled(playerID, removedCard, networkManager);
                        GameLogic.cardHasBeenPulled(playerManager.getPlayer(playerID).getPlayer(), removedCard, networkManager, discardPile, turnManager);
                    }
                }
            }
        }
    }

    private void handleDistributeDeckMessage(String text) {
        if (Message.parseAndExtractMessageID(text) == GAME_ACTIVITY_DECK_MESSAGE_ID) {
            Deck newDeck = new Deck(Message.parseAndExtractPayload(text));
            if (NetworkManager.isServer(networkManager)) {
                distributeDeck(networkManager, newDeck);
            }
        }
    }

    private void handleShowThreeCardsMessage(String text) {
        if (Message.parseAndExtractMessageID(text) == GAME_ACTIVITY_SHOW_THREE_CARDS_ID) {
            int playerID = Integer.parseInt(Message.parseAndExtractPayload(text));
            if (NetworkManager.isServer(networkManager)) {
                showTopThreeCards(playerID, networkManager);
            }
        }
    }

    private void handleFavorCardMessage(String text) {
        if (Message.parseAndExtractMessageID(text) == GAME_ACTIVITY_STEAL_CARD) {
            String fromPlayerIDToPlayerID = Message.parseAndExtractPayload(text);
            if (NetworkManager.isServer(networkManager)) {
                sendGiveAwayCard(fromPlayerIDToPlayerID, networkManager);
            }
        }
    }

    private void handleRecieveCard(String text) {
        if (Message.parseAndExtractMessageID(text) == PlayerMessageID.PLAYER_CARD_ADDED_MESSAGE_ID.id) {
            String[] message = Message.parseAndExtractPayload(text).split(":");
            int playerID = Integer.parseInt(message[0]);
            Card playedCard = Deck.getCardByID(Integer.parseInt(message[1]));
            if (NetworkManager.isServer(networkManager)) {
                sendCardTo(playerID, networkManager, playedCard);
            }
        }
    }

    public void distributeDeck(Deck deck) {
        try {
            if (deck != null) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_ACTIVITY_DECK_MESSAGE_ID, deck.deckToString()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
