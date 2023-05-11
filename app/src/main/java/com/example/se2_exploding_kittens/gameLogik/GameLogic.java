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

    public void nextPlayer() {
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
        do {
            currentPlayer++;
            if (currentPlayer >= playerList.size()) {
                currentPlayer = 0;
            }
        } while (!playerList.get(currentPlayer).alive);
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

    public void playCard(Card card) {
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

    }

    private void CatOneCard() {

    }

    private void CatTwoCard() {

    }

    private void CatThreeCard() {

    }

    private void CatFourCard() {

    }

    private void CatFiveCard() {

    }

    private void FavorCard() {

    }

    private void NopeCard() {

    }

    private void SeeTheFutureCard() {

    }

    private void SkipCard() {

    }

    private void ShuffleCard() {

    }


}
