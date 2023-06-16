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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    ArrayList<Card> cardDeck = new ArrayList<>();
    ArrayList<Card> cardDeckOld = new ArrayList<>();
    Random random;

    public Deck(long seed) {
        initAttackCard();
        //initCatCards();
        initFavorCard();
        initNopeCard();
        initShuffleCard();
        initSkipCard();
        initSeeTheFutureCard();
        this.random = new Random(seed);
        shuffleDeck();
    }

    public Deck(String exportString) {
        if (exportString != null) {
            String[] arr = exportString.split("-");
            Card card = null;
            for (String s : arr) {
                card = getCardByID(Integer.parseInt(s));
                if (card != null) {
                    cardDeck.add(card);
                }
            }
        }
    }

    public static Card getCardByID(int cardID) {
        Card card = null;
        switch (cardID) {
            case ATTACK_CARD_ID:
                card = (new AttackCard());
                break;
            case BOMB_CARD_ID:
                card = (new BombCard());
                break;
            case CAT_FIVE_CARD_ID:
                card = (new CatFiveCard());
                break;
            case CAT_FOUR_CARD_ID:
                card = (new CatFourCard());
                break;
            case CAT_ONE_CARD_ID:
                card = (new CatOneCard());
                break;
            case CAT_THREE_CARD_ID:
                card = (new CatThreeCard());
                break;
            case CAT_TWO_CARD_ID:
                card = (new CatTwoCard());
                break;
            case DEFUSE_CARD_ID:
                card = (new DefuseCard());
                break;
            case FAVOR_CARD_ID:
                card = (new FavorCard());
                break;
            case NOPE_CARD_ID:
                card = (new NopeCard());
                break;
            case SEE_THE_FUTURE_CARD_ID:
                card = (new SeeTheFutureCard());
                break;
            case SHUFFLE_CARD_ID:
                card = (new ShuffleCard());
                break;
            case SKIP_CARD_ID:
                card = (new SkipCard());
                break;
            default:
                break;
        }
        return card;
    }

    public String deckToString() {
        String export = "";
        for (Card c : cardDeck) {
            export = export + c.getCardID() + "-";
        }
        return export;
    }

    public void shuffleDeck() {
        cardDeckOld = cardDeck;
        Collections.shuffle(cardDeck);
    }

    public ArrayList<Integer> getNextThreeCardResources() {
        ArrayList<Integer> threeCards = new ArrayList<>();
        ArrayList<Card> tempDeck = (ArrayList<Card>) cardDeck.clone();
        for (int i = 0; i < 3; i++) {
            if (!tempDeck.isEmpty()) {
                threeCards.add(tempDeck.remove(0).getImageResource());
            } else {
                // If less than three cards
                threeCards.add(0);
            }
        }
        return threeCards;
    }

    public void insertCard(int cardID,int index) {
        cardDeck.add(index,getCardByID(cardID));
        cardDeckOld = (ArrayList<Card>) cardDeck.clone();
    }

    public void undoShuffle() {
        ArrayList<Card> tempDeck = cardDeck;
        cardDeck = cardDeckOld;
        cardDeckOld = tempDeck;
    }

    private void initAttackCard() {
        for (int i = 0; i < 4; i++) {
            cardDeck.add(new AttackCard());
        }
    }

    private void addBombCard(int amount) {
        for (int i = 0; i < amount; i++) {
            cardDeck.add(new BombCard());
        }
    }

    private void initCatCards() {
        for (int i = 0; i < 4; i++) {
            cardDeck.add(new CatOneCard());
            cardDeck.add(new CatTwoCard());
            cardDeck.add(new CatThreeCard());
            cardDeck.add(new CatFourCard());
            cardDeck.add(new CatFiveCard());
        }
    }

    private void addDefuseCard(int amount) {
        for (int i = 0; i < amount; i++) {
            cardDeck.add(new DefuseCard());
        }
    }

    private void initFavorCard() {
        for (int i = 0; i < 4; i++) {
            cardDeck.add(new FavorCard());
        }
    }

    private void initNopeCard() {
        for (int i = 0; i < 5; i++) {
            cardDeck.add(new NopeCard());
        }
    }

    private void initShuffleCard() {
        for (int i = 0; i < 4; i++) {
            cardDeck.add(new ShuffleCard());
        }
    }

    private void initSkipCard() {
        for (int i = 0; i < 4; i++) {
            cardDeck.add(new SkipCard());
        }
    }

    private void initSeeTheFutureCard() {
        for (int i = 0; i < 5; i++) {
            cardDeck.add(new SeeTheFutureCard());
        }
    }

    public void dealCards(ArrayList<Player> players) {
        for (Player player : players) {
            player.getHand().add(new DefuseCard());
            for (int i = 0; i < 7; i++) {
                player.getHand().add(getNextCard());
            }
        }

        addBombCard(players.size() - 1);
        addDefuseCard(6 - players.size());
        shuffleDeck();
    }

    public Card getNextCard() {
        if (cardDeck.size() > 0) {
            return cardDeck.remove(0);
        }
        throw new IndexOutOfBoundsException("The deck is empty!");
    }

    public Card removeCard(int cardID) {
        if (cardDeck.size() > 0 && cardDeck.get(0).getCardID() == cardID) {
            return cardDeck.remove(0);
        }
        throw new IndexOutOfBoundsException("The deck is empty, or card mismatch!");
    }

    public int addBombAtRandomIndex() {
        if (cardDeck.size() > 1) {
            int index = random.nextInt(cardDeck.size() - 1);
            cardDeck.add(index, new BombCard());
            return index;
        } else {
            cardDeck.add(0, new BombCard());
            return 0;
        }
    }
}