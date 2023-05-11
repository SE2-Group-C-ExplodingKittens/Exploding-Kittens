package com.example.se2_exploding_kittens.gameLogik;

import com.example.se2_exploding_kittens.gameLogik.cards.AttackCard;
import com.example.se2_exploding_kittens.gameLogik.cards.BombCard;
import com.example.se2_exploding_kittens.gameLogik.cards.Card;
import com.example.se2_exploding_kittens.gameLogik.cards.CatFiveCard;
import com.example.se2_exploding_kittens.gameLogik.cards.CatFourCard;
import com.example.se2_exploding_kittens.gameLogik.cards.CatOneCard;
import com.example.se2_exploding_kittens.gameLogik.cards.CatThreeCard;
import com.example.se2_exploding_kittens.gameLogik.cards.CatTwoCard;
import com.example.se2_exploding_kittens.gameLogik.cards.FavorCard;
import com.example.se2_exploding_kittens.gameLogik.cards.NopeCard;
import com.example.se2_exploding_kittens.gameLogik.cards.SeeTheFutureCard;
import com.example.se2_exploding_kittens.gameLogik.cards.ShuffleCard;
import com.example.se2_exploding_kittens.gameLogik.cards.SkipCard;

import java.util.ArrayList;

public class GameLogic {

    private boolean playsTwice = false;
    Card cardToBePlayed;
    boolean cardIsGoingToBeBPlayed;
    private final ArrayList<Card> currentPlayerPlayedCards = new ArrayList<>();
    ArrayList<Player> playerList = new ArrayList<>();
    int idOfLocalPlayer;
    Deck deck;
    int currentPlayer = 0;

    public GameLogic(int numOfPlayers, int idOfLocalPlayer, int seed) {
        initPlayers(numOfPlayers);
        this.idOfLocalPlayer = idOfLocalPlayer;
        this.deck = new Deck(seed);
        deck.dealCards(playerList);
    }

    public void endTurn() {
        Card card = deck.getNextCard();
        if (card instanceof BombCard) {
            if (playerList.get(currentPlayer).getDefuse() != -1) {
                playerList.get(currentPlayer).hand.remove(playerList.get(currentPlayer).getDefuse());
                deck.addBombAtRandomIndex();
            } else {
                playerList.get(currentPlayer).alive = false;
                checkIfLastSurvivor();
            }
        } else {
            playerList.get(currentPlayer).hand.add(card);
        }
        nextPlayer();
    }

    public void nextPlayer() {
        if (playsTwice) {
            playsTwice = false;
        } else {
            do {
                currentPlayer++;
                if (currentPlayer >= playerList.size()) {
                    currentPlayer = 0;
                }
            } while (!playerList.get(currentPlayer).alive);
        }
        currentPlayerPlayedCards.clear();
    }

    private void checkIfLastSurvivor() {
        int counter = 0;
        for (Player player : playerList) {
            if (player.alive) {
                counter++;
            }
        }
        if (counter <= 1) {
            endGame();
        }
    }

    private void endGame() {
        //TODO end the game
    }

    private void initPlayers(int numOfPlayers) {
        for (int i = 0; i < numOfPlayers; i++) {
            playerList.add(new Player(i));
        }
    }

    public boolean cheat(int playerID) {
        for (Player player : playerList) {
            if (playerID == player.playerId) {
                player.hand.add(deck.getNextCard()); //Theoretically this could throw and exception, but practically this must not happen, so I am not catching it.
                return true;
            }
        }
        return false;
    }

    public boolean initializingPlay(Card card, int playerID) {
        if (currentPlayer == playerID && canNobodyNope()) {
            cardToBePlayed = card;
            cardIsGoingToBeBPlayed = true;
            for (Player player : playerList) {
                if (player.hand.contains(new NopeCard())) {
                    player.canNope = true;
                }
            }
            if (canNobodyNope() && cardIsGoingToBeBPlayed) {
                playCard(cardToBePlayed);
            }
            return true;
        }
        return false;
    }

    public void playerDoesNotNope(int playerID) {
        playerList.get(playerID).canNope = false;
        if (canNobodyNope() && cardIsGoingToBeBPlayed) {
            playCard(cardToBePlayed);
        }
    }

    public void playerDoesNope(int playerID) {
        playerList.get(playerID).canNope = false;
        cardIsGoingToBeBPlayed = !cardIsGoingToBeBPlayed;
        playerList.get(playerID).hand.remove(new NopeCard());
        currentPlayerPlayedCards.add(new NopeCard());
        if (canNobodyNope() && cardIsGoingToBeBPlayed) {
            playCard(cardToBePlayed);
        }
    }

    private boolean canNobodyNope() {
        for (Player player : playerList) {
            if (player.canNope) {
                return false;
            }
        }
        return true;
    }

    private void playCard(Card card) {
        if (card instanceof AttackCard) {
            playAttackCard();
        } else if (card instanceof CatOneCard) {
            CatOneCard();
        } else if (card instanceof CatTwoCard) {
            CatTwoCard();
        } else if (card instanceof CatThreeCard) {
            CatThreeCard();
        } else if (card instanceof CatFourCard) {
            CatFourCard();
        } else if (card instanceof CatFiveCard) {
            CatFiveCard();
        } else if (card instanceof FavorCard) {
            FavorCard();
        } else if (card instanceof NopeCard) {
            NopeCard();
        } else if (card instanceof SeeTheFutureCard) {
            SeeTheFutureCard();
        } else if (card instanceof ShuffleCard) {
            ShuffleCard();
        } else if (card instanceof SkipCard) {
            SkipCard();
        }
    }

    private void playAttackCard() {
        nextPlayer();
        playsTwice = true;
    }

    private void CatOneCard() {
        if (currentPlayerPlayedCards.contains(new CatOneCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatOneCard());
        } else {
            currentPlayerPlayedCards.add(new CatOneCard());
        }
    }

    private void CatTwoCard() {
        if (currentPlayerPlayedCards.contains(new CatTwoCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatTwoCard());
        } else {
            currentPlayerPlayedCards.add(new CatTwoCard());
        }
    }

    private void CatThreeCard() {
        if (currentPlayerPlayedCards.contains(new CatThreeCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatThreeCard());
        } else {
            currentPlayerPlayedCards.add(new CatThreeCard());
        }
    }

    private void CatFourCard() {
        if (currentPlayerPlayedCards.contains(new CatFourCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatFourCard());
        } else {
            currentPlayerPlayedCards.add(new CatFourCard());
        }
    }

    private void CatFiveCard() {
        if (currentPlayerPlayedCards.contains(new CatFiveCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatFiveCard());
        } else {
            currentPlayerPlayedCards.add(new CatFiveCard());
        }
    }

    private void FavorCard() {
        choosePlayerToGiveCard();
        currentPlayerPlayedCards.add(new FavorCard());
    }

    private void NopeCard() {
        currentPlayerPlayedCards.add(new NopeCard());
    }

    private void SeeTheFutureCard() {
        currentPlayerPlayedCards.add(new SeeTheFutureCard());
        showTopThreeCards();
    }

    private void SkipCard() {
        currentPlayerPlayedCards.add(new SkipCard());
        nextPlayer();
    }

    private void ShuffleCard() {
        currentPlayerPlayedCards.add(new ShuffleCard());
        deck.shuffleDeck();
    }

    private void stealRandomCard() {
        //TODO
    }

    private void choosePlayerToGiveCard() {
        //TODO
    }

    private void showTopThreeCards() {
        //TODO
    }
}