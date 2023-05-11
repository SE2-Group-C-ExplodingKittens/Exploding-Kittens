package com.example.se2_exploding_kittens.gameLogik;

import com.example.se2_exploding_kittens.gameLogik.cards.AttackCard;
import com.example.se2_exploding_kittens.gameLogik.cards.BombCard;
import com.example.se2_exploding_kittens.gameLogik.cards.Cards;
import com.example.se2_exploding_kittens.gameLogik.cards.CatCard;
import com.example.se2_exploding_kittens.gameLogik.cards.DefuseCard;
import com.example.se2_exploding_kittens.gameLogik.cards.FavorCard;
import com.example.se2_exploding_kittens.gameLogik.cards.FunnyPingoCard;
import com.example.se2_exploding_kittens.gameLogik.cards.NopeCard;
import com.example.se2_exploding_kittens.gameLogik.cards.SeeTheFutureCard;
import com.example.se2_exploding_kittens.gameLogik.cards.ShuffleCard;
import com.example.se2_exploding_kittens.gameLogik.cards.SkipCard;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    ArrayList<Cards> deck = new ArrayList<>();

    public Deck() {
        initAttackCard();
        initCatCard();
        initFunnyPingoCard();
        initFavorCard();
        initNopeCard();
        initShuffleCard();
        initSkipCard();
        initSeeTheFutureCard();
        // TODO defuse cards should not be in the initial Deck implement the returning of the remaining cards into the gameDeck
    }

    public void shuffleDeck(ArrayList<Cards> deck) {
        this.deck = deck;
        Collections.shuffle(deck);
    }

    public void initAttackCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(new AttackCard());
        }
    }

    public void initBombCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(new BombCard());
        }
    }

    public void initCatCard() {
        for (int i = 0; i < 10; i++) {
            deck.add(new CatCard());
        }
    }

    public void initFunnyPingoCard() {
        for (int i = 0; i < 10; i++) {
            deck.add(new FunnyPingoCard());
        }
    }

    public void initDefuseCard(int nrOfCards) {
        for (int i = 0; i < nrOfCards; i++) {
            deck.add(new DefuseCard());
        }
    }

    public void initFavorCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(new FavorCard());
        }
    }

    public void initNopeCard() {
        for (int i = 0; i < 5; i++) {
            deck.add(new NopeCard());
        }
    }

    public void initShuffleCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(new ShuffleCard());
        }
    }

    public void initSkipCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(new SkipCard());
        }
    }

    public void initSeeTheFutureCard() {
        for (int i = 0; i < 5; i++) {
            deck.add(new SeeTheFutureCard());
        }
    }

    public ArrayList<Cards> getDeck() {
        return deck;
    }

    public void dealCards(ArrayList<Player> players) {
        // shuffle the deck
        Collections.shuffle(deck);

        // the number of cards to deal to each player
        int numCardsPerPlayer = 6;

        // create a list to store the dealt cards
        ArrayList<Cards> dealtCards = new ArrayList<Cards>();

        // deal cards to each player
        for (Player player : players) {
            ArrayList<Cards> playerHand = new ArrayList<Cards>(deck.subList(0, numCardsPerPlayer));
            //Adding defuse card to each players deck
            playerHand.add(new DefuseCard());
            player.setPlayerHand(playerHand);
            dealtCards.addAll(playerHand);
            deck.subList(0, numCardsPerPlayer).clear();
        }

        // Add 4 bomb cards into the deck again
        initBombCard();

        //Return the defuse cards into the deck
        if (players.size() < 5) {
            initDefuseCard(2);
        } else if (players.size() == 5) {
            initDefuseCard(1);
        }
        // Shuffle the deck before starting the game
        Collections.shuffle(deck);

    }

    // TODO write the exception for this method
    public Cards getNextCard() {
        if (deck.size() > 0) {
            Cards card = deck.remove(0);
            return card;
        }
        throw new IndexOutOfBoundsException("The deck is empty!");
    }

    public void test() {
        Deck deck = new Deck();
        for (int i = 0; i < deck.getDeck().size(); i++) {
            System.out.println(deck.getDeck().indexOf(i));
        }

    }

}
