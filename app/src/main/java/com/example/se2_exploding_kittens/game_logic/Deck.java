package com.example.se2_exploding_kittens.game_logic;

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
    Random random;

    public Deck(int seed) {
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

    public void shuffleDeck() {
        ArrayList<Card> tempDeck = new ArrayList<>();
        while (!cardDeck.isEmpty()) {
            tempDeck.add(cardDeck.remove(random.nextInt(cardDeck.size() + 1)));
        }
        cardDeck = tempDeck;
    }

    // FIXME extract magic constants (to describe meaning of 4)
    // FIXME method names plural
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

    public void addBombAtRandomIndex(){
        cardDeck.add(random.nextInt(cardDeck.size()+1), new BombCard());
    }
}