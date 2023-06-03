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
import java.util.Random;

public class Deck {
    ArrayList<Card> cardDeck = new ArrayList<>();
    ArrayList<Card> cardDeckOld = new ArrayList<>();
    Random random;

    public Deck(long seed) {
        initAttackCard();
        initCatCards();
        initFavorCard();
        initNopeCard();
        initShuffleCard();
        initSkipCard();
        initSeeTheFutureCard();
        this.random = new Random(seed);
        shuffleDeck();
    }

    public Deck(String exportString) {
        if(exportString != null){
            String[] arr = exportString.split("-");
            for(String s: arr){
                switch (Integer.parseInt(s)){
                    case ATTACK_CARD_ID:
                        cardDeck.add(new AttackCard());
                        break;
                    case BOMB_CARD_ID:
                        cardDeck.add(new BombCard());
                        break;
                    case CAT_FIVE_CARD_ID:
                        cardDeck.add(new CatFiveCard());
                        break;
                    case CAT_FOUR_CARD_ID:
                        cardDeck.add(new CatFourCard());
                        break;
                    case CAT_ONE_CARD_ID:
                        cardDeck.add(new CatOneCard());
                        break;
                    case CAT_THREE_CARD_ID:
                        cardDeck.add(new CatThreeCard());
                        break;
                    case CAT_TWO_CARD_ID:
                        cardDeck.add(new CatTwoCard());
                        break;
                    case DEFUSE_CARD_ID:
                        cardDeck.add(new DefuseCard());
                        break;
                    case FAVOR_CARD_ID:
                        cardDeck.add(new FavorCard());
                        break;
                    case NOPE_CARD_ID:
                        cardDeck.add(new NopeCard());
                        break;
                    case SEE_THE_FUTURE_CARD_ID:
                        cardDeck.add(new SeeTheFutureCard());
                        break;
                    case SHUFFLE_CARD_ID:
                        cardDeck.add(new ShuffleCard());
                        break;
                    case SKIP_CARD_ID:
                        cardDeck.add(new SkipCard());
                        break;
                }
            }
        }
    }

    public String deckToString(){
        String export = "";
        for (Card c: cardDeck) {
            export = export+c.getCardID()+"-";
        }
        return export;
    }

    public void shuffleDeck() {
        cardDeckOld = cardDeck;
        ArrayList<Card> tempDeck = new ArrayList<>();
        while (!cardDeck.isEmpty()) {
            if(cardDeck.size() > 1){
                tempDeck.add(cardDeck.remove(random.nextInt(cardDeck.size() - 1)));
            }else {
                tempDeck.add(cardDeck.remove(0));
            }

        }
        cardDeck = tempDeck;
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

    public void addBombAtRandomIndex(){
        if(cardDeck.size() > 1){
            cardDeck.add(random.nextInt(cardDeck.size()-1), new BombCard());
        }else {
            cardDeck.add(0, new BombCard());
        }
    }
}