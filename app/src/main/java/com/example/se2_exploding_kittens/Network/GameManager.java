package com.example.se2_exploding_kittens.Network;

import static com.example.se2_exploding_kittens.GameActivity.GAME_ACTIVITY_DECK_MESSAGE_ID;
import static com.example.se2_exploding_kittens.GameActivity.GAME_ACTIVITY_SHOW_THREE_CARDS_ID;
import static com.example.se2_exploding_kittens.game_logic.PlayerMessageID.PLAYER_HAND_MESSAGE_ID;

import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.cards.Card;

public class GameManager implements MessageCallback {

    private final NetworkManager networkManager;
    private final TurnManager turnManager;
    private final Deck deck;
    private final PlayerManager playerManager;
    private final DiscardPile discardPile;
    public static final int GAME_MANAGER_MESSAGE_CARD_PULLED_ID = 501;
    public static final int GAME_MANAGER_MESSAGE_CARD_PLAYED_ID = 503;
    public static final int GAME_MANAGER_MESSAGE_BOMB_PULLED_ID = 504;
    public static final int GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID = 506;
    public static final int GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID = 507;
    public static final int GAME_MANAGER_MESSAGE_CHECKED_CARD = 508;

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
    }

    public TurnManager getTurnManage() {
        return turnManager;
    }

    public void startGame() {
        turnManager.startGame();
    }

    public void distributePlayerHands() {
        try {
            if (playerManager != null && networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
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

    public static void sendNopeEnabled(NetworkManager networkManager) {
        try {
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                GameLogic.nopeEnabled = true;
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID, ""));
            } else if (networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                GameLogic.nopeEnabled = true;
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID, ""));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendNopeDisabled(NetworkManager networkManager) {
        try {
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                GameLogic.nopeEnabled = false;
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID, ""));
            } else if (networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                GameLogic.nopeEnabled = false;
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID, ""));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendCardPulled(int playerID, Card card, NetworkManager networkManager) {
        try {
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PULLED_ID, card.getCardID() + ":" + playerID));

            } else if (networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PULLED_ID, card.getCardID() + ":" + playerID));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendBombPulled(int playerID, Card card, NetworkManager networkManager) {
        try {
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID, card.getCardID() + ":" + playerID));

            } else if (networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_BOMB_PULLED_ID, card.getCardID() + ":" + playerID));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendCardPlayed(int playerID, Card card, NetworkManager networkManager) {
        try {
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID, card.getCardID() + ":" + playerID));

            } else if (networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_MANAGER_MESSAGE_CARD_PLAYED_ID, card.getCardID() + ":" + playerID));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void distributeDeck(NetworkManager networkManager, Deck deck) {
        try {
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_ACTIVITY_DECK_MESSAGE_ID, deck.deckToString()));
            } else if (networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_ACTIVITY_DECK_MESSAGE_ID, deck.deckToString()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void showTopThreeCards(int playerID, NetworkManager networkManager) {
        try {
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                networkManager.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_ACTIVITY_SHOW_THREE_CARDS_ID, Integer.toString(playerID)));
            } else if (networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                networkManager.sendMessageFromTheClient(new Message(MessageType.MESSAGE, GAME_ACTIVITY_SHOW_THREE_CARDS_ID, Integer.toString(playerID)));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void responseReceived(String text, Object sender) {
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PULLED_ID) {
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2) {
                int playerID = Integer.parseInt(message[1]);
                if (playerID != playerManager.getLocalSelf().getPlayerId()) {
                    Card removedCard = deck.removeCard(Integer.parseInt(message[0]));
                    if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                        //broadcast to other clients
                        sendCardPulled(playerID, removedCard, networkManager);
                        GameLogic.cardHasBeenPulled(playerManager.getPlayer(playerID).getPlayer(), removedCard, networkManager, discardPile, turnManager);
                        //playerManager.getPlayer(playerID).getPlayer().addCardToHand(Integer.toString(removedCard.getCardID()));
                    }
                }
            }
        }
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_BOMB_PULLED_ID) {
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2) {
                int playerID = Integer.parseInt(message[1]);
                if (playerID != playerManager.getLocalSelf().getPlayerId()) {
                    Card removedCard = deck.removeCard(Integer.parseInt(message[0]));
                    if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                        //broadcast to other clients
                        sendBombPulled(playerID, removedCard, networkManager);
                        GameLogic.cardHasBeenPulled(playerManager.getPlayer(playerID).getPlayer(), removedCard, networkManager, discardPile, turnManager);
                        //playerManager.getPlayer(playerID).getPlayer().setHasBomb(true);
                    }
                }
            }
        }
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_CARD_PLAYED_ID) {
            String[] message = Message.parseAndExtractPayload(text).split(":");
            if (message.length == 2) {
                int playerID = Integer.parseInt(message[1]);
                if (playerID != playerManager.getLocalSelf().getPlayerId()) {

                    Card playedCard = Deck.getCardByID(Integer.parseInt(message[0]));
                    //discardPile.putCard(Integer.parseInt(message[0]));

                    if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                        //broadcast to other clients
                        sendCardPlayed(playerID, playedCard, networkManager);
                        GameLogic.cardHasBeenPlayed(playerManager.getPlayer(playerID).getPlayer(), playedCard, networkManager, discardPile, turnManager, deck, null);
                        //playerManager.getPlayer(playerID).getPlayer().removeCardFromHand(Integer.toString(playedCard.getCardID()));
                    }
                }
            }
        }
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_NOPE_ENABLED_ID) {
            GameLogic.nopeEnabled = true;
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                //broadcast to other clients
                sendNopeEnabled(networkManager);
            }
        }
        if (Message.parseAndExtractMessageID(text) == GAME_MANAGER_MESSAGE_NOPE_DISABLED_ID) {
            GameLogic.nopeEnabled = false;
            if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                //broadcast to other clients
                sendNopeDisabled(networkManager);
            }
        }
    }
}
