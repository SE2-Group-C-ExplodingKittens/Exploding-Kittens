package com.example.se2_exploding_kittens.cards.Deck;

import com.example.se2_exploding_kittens.cards.CardFactory;
import com.example.se2_exploding_kittens.cards.Cards;
import com.example.se2_exploding_kittens.cards.DefuseCard;
import com.example.se2_exploding_kittens.cards.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    ArrayList<Cards> deck = new ArrayList<>();
    public CardFactory factory = new CardFactory();

    public Deck() {
        initAttackCard();
        initCatCard();
        initFunnyPingoCard();
        initFavorCard();
        initNopeCard();
        initShuffleCard();
        initSkipCard();
        // TODO defuse cards should not be in the initial Deck implement the returning of the remaining cards into the gameDeck
    }

    public void shuffleDeck(ArrayList<Cards> deck) {
        this.deck = deck;
        Collections.shuffle(deck);
    }

    public void initAttackCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("ATTACKCARD"));
        }
    }

    public void initBombCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("BOMBCARD"));
        }
    }

    public void initCatCard() {
        for (int i = 0; i < 10; i++) {
            deck.add(factory.getCard("CATCARD"));
        }
    }
    public void initFunnyPingoCard() {
        for (int i = 0; i < 10; i++) {
            deck.add(factory.getCard("FUNNYPINGO"));
        }
    }

    public void initDefuseCard() {
        for (int i = 0; i < 6; i++) {
            deck.add(factory.getCard("DEFUSECARD"));
        }
    }

    public void initFavorCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("FAVORCARD"));
        }
    }

    public void initNopeCard() {
        for (int i = 0; i < 5; i++) {
            deck.add(factory.getCard("NOPECARD"));
        }
    }

    public void initShuffleCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("SHUFFLECARD"));
        }
    }

    public void initSkipCard() {
        for (int i = 0; i < 4; i++) {
            deck.add(factory.getCard("SKIPCARD"));
        }
    }

    public ArrayList<Cards> getDeck() {
        return deck;
    }

    public List<Cards> dealCards(List<Player> players) {
        // shuffle the deck
        Collections.shuffle(deck);

        // the number of cards to deal to each player
        int numCardsPerPlayer = 6;

        // create a list to store the dealt cards
        ArrayList<Cards> dealtCards = new ArrayList<Cards>();

        // deal cards to each player
        for (Player player : players) {
            ArrayList<Cards> playerHand = new ArrayList<Cards>(deck.subList(0, numCardsPerPlayer - 1));
            //Adding defuse card to each players deck
            playerHand.add(factory.getCard("DEFUSECARD"));
            player.setPlayerHand(playerHand);
            dealtCards.addAll(playerHand);
            deck.subList(0, numCardsPerPlayer - 1).clear();
        }

        return dealtCards;
    }


    public ArrayList<String> getCardName() {
        ArrayList<String> cardNames = new ArrayList<String>();
        for (Cards card : deck) {
            cardNames.add(card.getCardName());
        }
        return cardNames;
    }

    public void test() {
        Deck deck = new Deck();
        for (int i = 0; i < deck.getDeck().size(); i++) {
            System.out.println(deck.getDeck().indexOf(i));
        }

    }

}
