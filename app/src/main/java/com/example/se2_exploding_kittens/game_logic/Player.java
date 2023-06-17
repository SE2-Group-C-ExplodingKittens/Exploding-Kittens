package com.example.se2_exploding_kittens.game_logic;

import static com.example.se2_exploding_kittens.game_logic.PlayerMessageID.PLAYER_CARD_ADDED_MESSAGE_ID;
import static com.example.se2_exploding_kittens.game_logic.PlayerMessageID.PLAYER_CARD_REMOVED_MESSAGE_ID;
import static com.example.se2_exploding_kittens.game_logic.PlayerMessageID.PLAYER_HAND_MESSAGE_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.AttackCard.ATTACK_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.BombCard.BOMB_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.CatFiveCard.CAT_FIVE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.CatFourCard.CAT_FOUR_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.CatOneCard.CAT_ONE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.CatThreeCard.CAT_THREE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.CatTwoCard.CAT_TWO_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.DefuseCard.DEFUSE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.FavorCard.FAVOR_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.NopeCard.NOPE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.SeeTheFutureCard.SEE_THE_FUTURE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.ShuffleCard.SHUFFLE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.SkipCard.SKIP_CARD_ID;

import android.database.Observable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.NetworkManager;
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Random;

public class Player implements MessageCallback {

    private int playerId;
    private boolean alive = true;
    private boolean hasBomb = false;
    private boolean canNope = false;
    private boolean hasWon = false;
    private int playerTurns;
    private static String DEBUG_TAG = "Player";
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    public static final String PLAYER_CARD_HAND_REMOVED_PROPERTY = "handCardRemoved";

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    // if the client initalizes a player object, ID may NOT be known yet thus getter and setter may be needed
    public Player(int playerId) {
        this.playerId = playerId;
    }

    public Player() {
        playerId = -1;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private ArrayList<Card> hand = new ArrayList<>();

    public int getDefuse() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) instanceof DefuseCard) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setPlayerTurns(int numTurns) {
        playerTurns = numTurns;
        if (playerTurns > 0) {
            //if it's players turn
            propertyChangeSupport.firePropertyChange("yourTurn", null, playerId);
        } else if (playerTurns == 0) {
            //if it's not players turn
            propertyChangeSupport.firePropertyChange("notYourTurn", null, playerId);
        }
    }

    public int getPlayerTurns() {
        return playerTurns;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String handToString() {
        String export = "";
        StringBuilder bld = new StringBuilder();
        if (hand.size() > 0) {
            for (Card c : hand) {
                bld.append(c.getCardID());
                bld.append("-");
            }
        }
        export = bld.toString();
        return export;
    }

    public void setHandFromString(String exportString) {
        if (exportString != null) {
            hand = new ArrayList<>();
            String[] arr = exportString.split("-");
            for (String s : arr) {
                addCardToHand(s);
            }
        }
    }

    public void subscribePlayerToCardEvents(NetworkManager connection) {
        if (connection != null && (NetworkManager.isNotIdle(connection))) {
            connection.subscribeCallbackToMessageID(this, PLAYER_HAND_MESSAGE_ID.id);
            connection.subscribeCallbackToMessageID(this, PLAYER_CARD_ADDED_MESSAGE_ID.id);
            connection.subscribeCallbackToMessageID(this, PLAYER_CARD_REMOVED_MESSAGE_ID.id);

        }
    }

    public void addCardToHand(String cardID) {
        if (cardID != null) {
            switch (Integer.parseInt(cardID)) {
                case ATTACK_CARD_ID:
                    hand.add(new AttackCard());
                    break;
                case BOMB_CARD_ID:
                    hand.add(new BombCard());
                    break;
                case CAT_FIVE_CARD_ID:
                    hand.add(new CatFiveCard());
                    break;
                case CAT_FOUR_CARD_ID:
                    hand.add(new CatFourCard());
                    break;
                case CAT_ONE_CARD_ID:
                    hand.add(new CatOneCard());
                    break;
                case CAT_THREE_CARD_ID:
                    hand.add(new CatThreeCard());
                    break;
                case CAT_TWO_CARD_ID:
                    hand.add(new CatTwoCard());
                    break;
                case DEFUSE_CARD_ID:
                    hand.add(new DefuseCard());
                    break;
                case FAVOR_CARD_ID:
                    hand.add(new FavorCard());
                    break;
                case NOPE_CARD_ID:
                    hand.add(new NopeCard());
                    break;
                case SEE_THE_FUTURE_CARD_ID:
                    hand.add(new SeeTheFutureCard());
                    break;
                case SHUFFLE_CARD_ID:
                    hand.add(new ShuffleCard());
                    break;
                case SKIP_CARD_ID:
                    hand.add(new SkipCard());
                    break;
                default:
                    break;
            }
        }
    }

    private void searchInHandAndRemove(ArrayList<Card>hand, int cardID){
        for (Card c: hand) {
            if(c.getCardID() == cardID){
                propertyChangeSupport.firePropertyChange(PLAYER_CARD_HAND_REMOVED_PROPERTY,null,hand.indexOf(c));
                hand.remove(c);
                return;
            }
        }
    }

    public void removeCardFromHand(String cardID) {
        if (cardID != null) {
            switch (Integer.parseInt(cardID)) {
                case ATTACK_CARD_ID:
                    searchInHandAndRemove(hand, ATTACK_CARD_ID);
                    break;
                case BOMB_CARD_ID:
                    searchInHandAndRemove(hand, BOMB_CARD_ID);
                    break;
                case CAT_FIVE_CARD_ID:
                    searchInHandAndRemove(hand, CAT_FIVE_CARD_ID);
                    break;
                case CAT_FOUR_CARD_ID:
                    searchInHandAndRemove(hand, CAT_FOUR_CARD_ID);
                    break;
                case CAT_ONE_CARD_ID:
                    searchInHandAndRemove(hand, CAT_ONE_CARD_ID);
                    break;
                case CAT_THREE_CARD_ID:
                    searchInHandAndRemove(hand, CAT_THREE_CARD_ID);
                    break;
                case CAT_TWO_CARD_ID:
                    searchInHandAndRemove(hand, CAT_TWO_CARD_ID);
                    break;
                case DEFUSE_CARD_ID:
                    searchInHandAndRemove(hand, DEFUSE_CARD_ID);
                    break;
                case FAVOR_CARD_ID:
                    searchInHandAndRemove(hand, FAVOR_CARD_ID);
                    break;
                case NOPE_CARD_ID:
                    searchInHandAndRemove(hand, NOPE_CARD_ID);
                    break;
                case SEE_THE_FUTURE_CARD_ID:
                    searchInHandAndRemove(hand, SEE_THE_FUTURE_CARD_ID);
                    break;
                case SHUFFLE_CARD_ID:
                    searchInHandAndRemove(hand, SHUFFLE_CARD_ID);
                    break;
                case SKIP_CARD_ID:
                    searchInHandAndRemove(hand, SKIP_CARD_ID);
                    break;
                default:
                    break;
            }
        }
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean isCanNope() {
        return canNope;
    }

    public void setCanNope(boolean canNope) {
        this.canNope = canNope;
    }

    public boolean isAlive() {
        return alive;
    }

    public Card removeRandomCardFromHand() {
        if (getHand().size() == 0) {
            return null;
        }

        Random random = new Random();
        //get random index from hand
        int randomIndex = random.nextInt(getHand().size());
        Card card = getHand().get(randomIndex);

        //remove card
        removeCardFromHand(Integer.toString(card.getCardID()));
        propertyChangeSupport.firePropertyChange("cardStolen", null, playerId);
        return card;
    }

    public void setAlive(boolean alive) {
        if (this.alive == true && alive == false){
            propertyChangeSupport.firePropertyChange("playerLost",true,false);
        }
        this.alive = alive;

    }

    private int getAddressedPlayerFromPayload(String payload) {
        String[] splitInput = payload.split(":");
        if (splitInput.length > 0) {
            try {
                return Integer.parseInt(splitInput[0]);
            } catch (NumberFormatException e) {
                Log.e(DEBUG_TAG, "Could not parse");
            }
        }
        return -1;  // -1 means invalid
    }


    private String parseDataFromPayload(String input) {
        String[] splitInput = input.split(":");
        if (splitInput.length > 1) {
            return splitInput[1];
        }
        return "";  // null means invalid
    }

    @Override
    public void responseReceived(String text, Object sender) {
        if (text != null) {
            int messageID = Message.parseAndExtractMessageID(text);
            String payload = Message.parseAndExtractPayload(text);
            if (playerId == getAddressedPlayerFromPayload(payload)) {
                ArrayList<Card> oldHand = new ArrayList<>(hand);
                if (messageID == PlayerMessageID.PLAYER_CARD_ADDED_MESSAGE_ID.id) {
                    addCardToHand(parseDataFromPayload(payload));
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            propertyChangeSupport.firePropertyChange("hand", oldHand, hand);
                        }
                    });
                } else if (messageID == PlayerMessageID.PLAYER_CARD_REMOVED_MESSAGE_ID.id) {
                    removeCardFromHand(parseDataFromPayload(payload));
                    propertyChangeSupport.firePropertyChange("hand", oldHand, hand);
                } else if (messageID == PlayerMessageID.PLAYER_HAND_MESSAGE_ID.id) {
                    setHandFromString(parseDataFromPayload(payload));
                    propertyChangeSupport.firePropertyChange("hand", oldHand, hand);
                }
            }
        }
    }

    public boolean isHasBomb() {
        return hasBomb;
    }

    public void setHasBomb(boolean hasBomb) {
        this.hasBomb = hasBomb;
    }

    public boolean isHasWon() {
        return hasWon;
    }

    public void setHasWon(boolean hasWon) {
        if (this.hasWon == false && hasWon == true){
            propertyChangeSupport.firePropertyChange("playerWon",-1,playerId);
        }
        this.hasWon = hasWon;
    }
}
