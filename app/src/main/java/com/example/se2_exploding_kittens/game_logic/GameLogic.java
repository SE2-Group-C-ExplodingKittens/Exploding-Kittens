package com.example.se2_exploding_kittens.game_logic;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.TurnManager;
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

public class GameLogic {

    private boolean playsTwice = false;
    public static boolean nopeEnabled = false;
    Card cardToBePlayed;
    boolean cardIsGoingToBeBPlayed;
    private final ArrayList<Card> currentPlayerPlayedCards = new ArrayList<>();
    ArrayList<Player> playerList = new ArrayList<>();
    int idOfLocalPlayer;
    Deck deck;
    int currentPlayer = 0;
    private GameManager gameManager;
    private static TurnManager turnManager;

    public GameLogic(int numOfPlayers, int idOfLocalPlayer, Deck deck, GameManager gameManager, TurnManager turnManager) {
        initPlayers(numOfPlayers);
        this.idOfLocalPlayer = idOfLocalPlayer;
        this.deck = deck;
        this.gameManager = gameManager;
        this.turnManager = turnManager;
        deck.dealCards(playerList);
    }

    public void endTurnByPullingCard() {
        Card card = deck.getNextCard();
        if (card instanceof BombCard) {
            if (playerList.get(currentPlayer).getDefuse() != -1) {
                playerList.get(currentPlayer).getHand().remove(playerList.get(currentPlayer).getDefuse());
                deck.addBombAtRandomIndex();
            } else {
                playerList.get(currentPlayer).setAlive(false);
                checkIfLastSurvivor();
            }
        } else {
            playerList.get(currentPlayer).getHand().add(card);
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
            } while (!playerList.get(currentPlayer).isAlive());
        }
        currentPlayerPlayedCards.clear();
    }

    private void checkIfLastSurvivor() {
        int counter = 0;
        for (Player player : playerList) {
            if (player.isAlive()) {
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
            if (playerID == player.getPlayerId()) {
                player.getHand().add(deck.getNextCard()); //Theoretically this could throw and exception, but practically this must not happen, so I am not catching it.
                return true;
            }
        }
        return false;
    }

    public boolean initializingPlay(Card card, int playerID) {
        if (currentPlayer == playerID && canNobodyNope()) {
            cardToBePlayed = card;
            cardIsGoingToBeBPlayed = true;
            /*for (Player player : playerList) {
                if (player.getHand().contains(new NopeCard())) {
                    player.setCanNope(true);
                }
            }*/
            if (canNobodyNope() && cardIsGoingToBeBPlayed) {
                playCard(cardToBePlayed);
            }
            return true;
        }
        return false;
    }

    public static boolean canCardBePlayed(Player player, Card card){
        //defuse can be played even if turns are 0
        if(!player.isAlive()){
            return false;
        }
        if(player.isHasBomb() && card instanceof DefuseCard){
            return true;
        }else if(!player.isHasBomb()){
            if(player.getPlayerTurns() > 0 || nopeEnabled && card instanceof NopeCard){
                if(player.getPlayerTurns() > 0){
                    //TODO some cards cant be played, like defuse if no bomb has been pulled

                    if(card instanceof SkipCard){
                        return true;
                    }
                }else if(nopeEnabled && card instanceof NopeCard){
                    return true;
                }
            }
        }

        return false;
    }

    public static void cardHasBeenPlayed(Player player, Card card, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager, Deck deck){
        if(card instanceof SkipCard){
            ((SkipCard) card).handleActions(player,networkManager,discardPile,turnManager);
        }else if(card instanceof DefuseCard){
            ((DefuseCard) card).handleActions(player,networkManager,discardPile,turnManager, deck);
        }
        else{
            if(player != null){
                GameManager.sendCardPlayed(player.getPlayerId(), card, networkManager);
            }
        }
    }

    public static boolean canCardBePulled(Player player){
        if(!player.isAlive()){
            return false;
        }
        if(player.getPlayerTurns() > 0){
            return true;
        }else {
            return false;
        }
    }

    public static void finishTurn(Player player, NetworkManager networkManager, int futureTurns, TurnManager turnManager){
        if(networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER && turnManager != null){
            TurnManager.broadcastTurnFinished(player,networkManager);
            turnManager.gameStateNextTurn(futureTurns);
        }
    }

    public static void cardHasBeenPulled(Player player, Card card, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager){
        player.setPlayerTurns(player.getPlayerTurns()-1);
        if(card instanceof BombCard){
            ((BombCard) card).handleActions(player,networkManager,discardPile,turnManager);
        }else{
            player.getHand().add(card);
            GameManager.sendCardPulled(player.getPlayerId(), card, networkManager);
            GameManager.sendNopeDisabled(networkManager);
            if(player.getPlayerTurns() == 0){
                finishTurn(player,networkManager,1, turnManager);
            }
        }
    }

    public void playerDoesNotNope(int playerID) {
/*
        playerList.get(playerID).setCanNope(false);
*/
        if (canNobodyNope() && cardIsGoingToBeBPlayed) {
            playCard(cardToBePlayed);
        }
    }

    public void playerDoesNope(int playerID) {
/*
        playerList.get(playerID).setCanNope(false);
*/
        cardIsGoingToBeBPlayed = !cardIsGoingToBeBPlayed;
        playerList.get(playerID).getHand().remove(new NopeCard());
        currentPlayerPlayedCards.add(new NopeCard());
        if (canNobodyNope() && cardIsGoingToBeBPlayed) {
            playCard(cardToBePlayed);
        }
    }

    private boolean canNobodyNope() {
        for (Player player : playerList) {
/*            if (player.isCanNope()) {
                return false;
            }*/
        }
        return true;
    }

    private void playCard(Card card) {
        if (card instanceof AttackCard) {
            playAttackCard();
        } else if (card instanceof CatOneCard) {
            catOneCard();
        } else if (card instanceof CatTwoCard) {
            catTwoCard();
        } else if (card instanceof CatThreeCard) {
            catThreeCard();
        } else if (card instanceof CatFourCard) {
            catFourCard();
        } else if (card instanceof CatFiveCard) {
            catFiveCard();
        } else if (card instanceof FavorCard) {
            favorCard();
        } else if (card instanceof NopeCard) {
            nopeCard();
        } else if (card instanceof SeeTheFutureCard) {
            seeTheFutureCard();
        } else if (card instanceof ShuffleCard) {
            shuffleCard();
        } else if (card instanceof SkipCard) {
            skipCard();
        }
    }

    private void playAttackCard() {
        nextPlayer();
        playsTwice = true;
    }

    private void catOneCard() {
        if (currentPlayerPlayedCards.contains(new CatOneCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatOneCard());
        } else {
            currentPlayerPlayedCards.add(new CatOneCard());
        }
    }

    private void catTwoCard() {
        if (currentPlayerPlayedCards.contains(new CatTwoCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatTwoCard());
        } else {
            currentPlayerPlayedCards.add(new CatTwoCard());
        }
    }

    private void catThreeCard() {
        if (currentPlayerPlayedCards.contains(new CatThreeCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatThreeCard());
        } else {
            currentPlayerPlayedCards.add(new CatThreeCard());
        }
    }

    private void catFourCard() {
        if (currentPlayerPlayedCards.contains(new CatFourCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatFourCard());
        } else {
            currentPlayerPlayedCards.add(new CatFourCard());
        }
    }

    private void catFiveCard() {
        if (currentPlayerPlayedCards.contains(new CatFiveCard())) {
            stealRandomCard();
            currentPlayerPlayedCards.remove(new CatFiveCard());
        } else {
            currentPlayerPlayedCards.add(new CatFiveCard());
        }
    }

    private void favorCard() {
        choosePlayerToGiveCard();
        currentPlayerPlayedCards.add(new FavorCard());
    }

    private void nopeCard() {
        currentPlayerPlayedCards.add(new NopeCard());
    }

    private void seeTheFutureCard() {
        currentPlayerPlayedCards.add(new SeeTheFutureCard());
        showTopThreeCards();
    }

    private void skipCard() {
        currentPlayerPlayedCards.add(new SkipCard());
        nextPlayer();
    }

    private void shuffleCard() {
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