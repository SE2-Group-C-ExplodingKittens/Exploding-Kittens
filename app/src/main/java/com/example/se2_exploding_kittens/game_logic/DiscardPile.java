package com.example.se2_exploding_kittens.game_logic;

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

public class DiscardPile {

    private ArrayList<Card> cardPile = new ArrayList<>();
    private ArrayList<Card> cardPileOld = new ArrayList<>();
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static final String DISCARD_PILE_PROPERTY = "discardPile";

    public DiscardPile() {

    }

    public ArrayList<Card> getCardPile() {
        return cardPile;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public DiscardPile(String exportString) {
        if (exportString != null) {
            String[] arr = exportString.split("-");
            for (String s : arr) {
                switch (Integer.parseInt(s)) {
                    case ATTACK_CARD_ID:
                        cardPile.add(new AttackCard());
                        break;
                    case BOMB_CARD_ID:
                        cardPile.add(new BombCard());
                        break;
                    case CAT_FIVE_CARD_ID:
                        cardPile.add(new CatFiveCard());
                        break;
                    case CAT_FOUR_CARD_ID:
                        cardPile.add(new CatFourCard());
                        break;
                    case CAT_ONE_CARD_ID:
                        cardPile.add(new CatOneCard());
                        break;
                    case CAT_THREE_CARD_ID:
                        cardPile.add(new CatThreeCard());
                        break;
                    case CAT_TWO_CARD_ID:
                        cardPile.add(new CatTwoCard());
                        break;
                    case DEFUSE_CARD_ID:
                        cardPile.add(new DefuseCard());
                        break;
                    case FAVOR_CARD_ID:
                        cardPile.add(new FavorCard());
                        break;
                    case NOPE_CARD_ID:
                        cardPile.add(new NopeCard());
                        break;
                    case SEE_THE_FUTURE_CARD_ID:
                        cardPile.add(new SeeTheFutureCard());
                        break;
                    case SHUFFLE_CARD_ID:
                        cardPile.add(new ShuffleCard());
                        break;
                    case SKIP_CARD_ID:
                        cardPile.add(new SkipCard());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public String pileToString() {
        String export = "";
        for (Card c : cardPile) {
            export = export + c.getCardID() + "-";
        }
        return export;
    }

    public void putCard(Card card) {
        cardPileOld = (ArrayList<Card>) cardPile.clone();
        cardPile.add(0, card);
        propertyChangeSupport.firePropertyChange(DISCARD_PILE_PROPERTY, cardPileOld, cardPile);
    }

    public Card putCard(int cardID) {
        cardPileOld = (ArrayList<Card>) cardPile.clone();
        switch (cardID) {
            case ATTACK_CARD_ID:
                cardPile.add(0, new AttackCard());
                break;
            case BOMB_CARD_ID:
                cardPile.add(0, new BombCard());
                break;
            case CAT_FIVE_CARD_ID:
                cardPile.add(0, new CatFiveCard());
                break;
            case CAT_FOUR_CARD_ID:
                cardPile.add(0, new CatFourCard());
                break;
            case CAT_ONE_CARD_ID:
                cardPile.add(0, new CatOneCard());
                break;
            case CAT_THREE_CARD_ID:
                cardPile.add(0, new CatThreeCard());
                break;
            case CAT_TWO_CARD_ID:
                cardPile.add(0, new CatTwoCard());
                break;
            case DEFUSE_CARD_ID:
                cardPile.add(0, new DefuseCard());
                break;
            case FAVOR_CARD_ID:
                cardPile.add(0, new FavorCard());
                break;
            case NOPE_CARD_ID:
                cardPile.add(0, new NopeCard());
                break;
            case SEE_THE_FUTURE_CARD_ID:
                cardPile.add(0, new SeeTheFutureCard());
                break;
            case SHUFFLE_CARD_ID:
                cardPile.add(0, new ShuffleCard());
                break;
            case SKIP_CARD_ID:
                cardPile.add(0, new SkipCard());
                break;
            default:
                break;
        }
        propertyChangeSupport.firePropertyChange(DISCARD_PILE_PROPERTY, cardPileOld, cardPile);
        return cardPile.get(0);
    }

    public void revertPile() {
        ArrayList<Card> tempPile = (ArrayList<Card>) cardPile.clone();
        cardPile = cardPileOld;
        cardPileOld = tempPile;
        propertyChangeSupport.firePropertyChange(DISCARD_PILE_PROPERTY, cardPileOld, cardPile);
    }

    public void pullCard(int index) {
        if (cardPile.size() >= (index)) {
            propertyChangeSupport.firePropertyChange(DISCARD_PILE_PROPERTY, cardPileOld, cardPile);
            cardPile.remove(index);
            return;
        }
        throw new IndexOutOfBoundsException("The pile has to few cards!");
    }

    public int getRandomCardIndex() {
        if (cardPile.size() == 0) {
            return -1;
        }

        Random random = new Random();
        return random.nextInt(cardPile.size());
    }

}
