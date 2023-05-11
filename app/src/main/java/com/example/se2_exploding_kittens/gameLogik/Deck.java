package com.example.se2_exploding_kittens.gameLogik;

import com.example.se2_exploding_kittens.gameLogik.cards.AttackCard;
import com.example.se2_exploding_kittens.gameLogik.cards.BombCard;
import com.example.se2_exploding_kittens.gameLogik.cards.Card;
import com.example.se2_exploding_kittens.gameLogik.cards.CatFiveCard;
import com.example.se2_exploding_kittens.gameLogik.cards.CatFourCard;
import com.example.se2_exploding_kittens.gameLogik.cards.CatOneCard;
import com.example.se2_exploding_kittens.gameLogik.cards.CatThreeCard;
import com.example.se2_exploding_kittens.gameLogik.cards.CatTwoCard;
import com.example.se2_exploding_kittens.gameLogik.cards.DefuseCard;
import com.example.se2_exploding_kittens.gameLogik.cards.FavorCard;
import com.example.se2_exploding_kittens.gameLogik.cards.NopeCard;
import com.example.se2_exploding_kittens.gameLogik.cards.SeeTheFutureCard;
import com.example.se2_exploding_kittens.gameLogik.cards.ShuffleCard;
import com.example.se2_exploding_kittens.gameLogik.cards.SkipCard;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
    ArrayList<Card> deck = new ArrayList<>();
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
        while (!deck.isEmpty()) {
            tempDeck.add(deck.remove(random.nextInt(deck.size() + 1)));
        }
        deck = tempDeck;
    }

    private void initAttackCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(new AttackCard());
        }
    }

    private void addBombCard(int amount) {
        for (int i = 0; i < amount; i++) {
            deck.add(new BombCard());
        }
    }

    private void initCatCards() {
        for (int i = 0; i < 4; i++) {
            deck.add(new CatOneCard());
            deck.add(new CatTwoCard());
            deck.add(new CatThreeCard());
            deck.add(new CatFourCard());
            deck.add(new CatFiveCard());
        }
    }

    private void addDefuseCard(int amount) {
        for (int i = 0; i < amount; i++) {
            deck.add(new DefuseCard());
        }
    }

    private void initFavorCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(new FavorCard());
        }
    }

    private void initNopeCard() {
        for (int i = 0; i < 5; i++) {
            deck.add(new NopeCard());
        }
    }

    private void initShuffleCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(new ShuffleCard());
        }
    }

    private void initSkipCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(new SkipCard());
        }
    }

    private void initSeeTheFutureCard() {
        for (int i = 0; i < 5; i++) {
            deck.add(new SeeTheFutureCard());
        }
    }

    public void dealCards(ArrayList<Player> players) {
        for (Player player : players) {
            player.hand.add(new DefuseCard());
            for (int i = 0; i < 7; i++) {
                player.hand.add(getNextCard());
            }
        }

        addBombCard(players.size() - 1);
        addDefuseCard(6 - players.size());
        shuffleDeck();
    }

    public Card getNextCard() {
        if (deck.size() > 0) {
            return deck.remove(0);
        }
        throw new IndexOutOfBoundsException("The deck is empty!");
    }

    public void addBombAtRandomIndex(){
        deck.add(random.nextInt(deck.size()+1), new BombCard());
    }
}