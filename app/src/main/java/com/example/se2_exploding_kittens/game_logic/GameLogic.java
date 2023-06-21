package com.example.se2_exploding_kittens.game_logic;

import android.content.Context;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
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
import java.util.Arrays;

public class GameLogic {

    public static boolean nopeEnabled = false;
    ArrayList<Player> playerList = new ArrayList<>();
    private static final ArrayList<String> playerIDList = new ArrayList<>();
    int idOfLocalPlayer;
    Deck deck;

    int numOfPlayer;

    public int getIdOfLocalPlayer() {
        return idOfLocalPlayer;
    }

    public int getNumOfPlayer() {
        return numOfPlayer;
    }

    public Deck getDeck() {
        return deck;
    }

    public static Card lastCardPlayedExceptNope;

    public GameLogic(int numOfPlayers, int idOfLocalPlayer, Deck deck) throws IllegalArgumentException {
        if (numOfPlayers < 2 || numOfPlayers > 5 || idOfLocalPlayer < 0 || idOfLocalPlayer >= numOfPlayers) {
            throw new IllegalArgumentException();
        } else {
            this.numOfPlayer = numOfPlayers;
            initPlayers(numOfPlayers);
            this.idOfLocalPlayer = idOfLocalPlayer;
            this.deck = deck;
            deck.dealCards(playerList);
        }
    }

    public static void setPlayerIDList(String text) throws IllegalArgumentException {
        String[] playerList = text.split(":");

        if (playerList.length < 2 || playerList.length > 5 || text.matches(".*::.*") || text.matches(":.*") || text.matches(".*:")) {
            throw new IllegalArgumentException();
        }

        //this should prevent, that artifacts form previous games persist
        playerIDList.clear();

        playerIDList.addAll(Arrays.asList(playerList));
        while (playerIDList.size() < 5) {
            playerIDList.add(null);
        }
    }

    public static ArrayList<String> getPlayerIDList() {
        return playerIDList;
    }

    private void initPlayers(int numOfPlayers) {
        for (int i = 0; i < numOfPlayers; i++) {
            playerList.add(new Player(i));
        }
    }

    public static boolean canCardBePlayed(Player player, Card card) {
        //defuse can be played even if turns are 0
        if (!player.isAlive()) {
            return false;
        }
        if (player.isHasWon()) {
            return false;
        }
        if (player.isHasBomb() && card instanceof DefuseCard) {
            return true;
        } else if (!player.isHasBomb() && (player.getPlayerTurns() > 0 || nopeEnabled && card instanceof NopeCard)) {

            if (player.getPlayerTurns() > 0 && (card instanceof SkipCard || card instanceof ShuffleCard || card instanceof AttackCard || card instanceof SeeTheFutureCard || card instanceof FavorCard
                    || card instanceof CatOneCard || card instanceof CatTwoCard || card instanceof CatThreeCard || card instanceof CatFourCard || card instanceof CatFiveCard)) {
                return true;
            }
            //nope is only affected if nopeEnabled == true, the turns of the player don't affect it
            return nopeEnabled && card instanceof NopeCard;
        }

        return false;
    }

    public static void cardHasBeenPlayed(Player player, Card card, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager, Deck deck, Context context) {
        if (card instanceof SkipCard) {
            lastCardPlayedExceptNope = card;
            ((SkipCard) card).handleActions(player, networkManager, discardPile, turnManager);
        } else if (card instanceof DefuseCard) {
            lastCardPlayedExceptNope = card;
            ((DefuseCard) card).handleActions(player, networkManager, discardPile, turnManager, deck);
        } else if (card instanceof ShuffleCard) {
            lastCardPlayedExceptNope = card;
            ((ShuffleCard) card).handleActions(player, networkManager, discardPile, deck);
        } else if (card instanceof AttackCard) {
            lastCardPlayedExceptNope = card;
            ((AttackCard) card).handleActions(player, networkManager, discardPile, turnManager);
        } else if (card instanceof SeeTheFutureCard) {
            lastCardPlayedExceptNope = card;
            ((SeeTheFutureCard) card).handleActions(player, networkManager, discardPile, deck, context);
        } else if (card instanceof FavorCard) {
            lastCardPlayedExceptNope = card;
            ((FavorCard) card).handleActions(player, networkManager, discardPile, context);
        } else if (card instanceof CatOneCard) {
            lastCardPlayedExceptNope = card;
            ((CatOneCard) card).handleActions(player, networkManager, discardPile, context);
        } else if (card instanceof CatTwoCard) {
            lastCardPlayedExceptNope = card;
            ((CatTwoCard) card).handleActions(player, networkManager, discardPile, context);
        } else if (card instanceof CatThreeCard) {
            lastCardPlayedExceptNope = card;
            ((CatThreeCard) card).handleActions(player, networkManager, discardPile, context);
        } else if (card instanceof CatFourCard) {
            lastCardPlayedExceptNope = card;
            ((CatFourCard) card).handleActions(player, networkManager, discardPile, context);
        } else if (card instanceof CatFiveCard) {
            lastCardPlayedExceptNope = card;
            ((CatFiveCard) card).handleActions(player, networkManager, discardPile, context);
        } else if (card instanceof NopeCard) {
            ((NopeCard) card).handleActions(player, networkManager, discardPile, turnManager, deck);
        } else {
            if (player != null) {
                GameManager.sendCardPlayed(player.getPlayerId(), card, networkManager);
            }
        }
    }

    public static boolean canCardBePulled(Player player) {
        if (!player.isAlive()) {
            return false;
        }
        if (player.isHasWon()) {
            return false;
        }
        if (player.isHasBomb()) {
            return false;
        }
        return player.getPlayerTurns() > 0;
    }

    public static int checkForWinner(PlayerManager playerManager) {
        if (playerManager.getPlayerSize() == 1) {
            return playerManager.getPlayers().get(0).getPlayerID();
        } else {
            int alivePlayers = 0;
            int winner = -1;
            for (PlayerConnection pc : playerManager.getPlayers()) {
                Player p = pc.getPlayer();
                if (p.isAlive()) {
                    winner = p.getPlayerId();
                    alivePlayers++;
                }
            }
            if (alivePlayers == 1) {
                return winner;
            }
        }
        return -1;
    }

    public static void updatePlayerHand(Player player, String playerHand) {
        player.setHandFromString(playerHand);
    }

    public static void finishTurn(Player player, NetworkManager networkManager, int futureTurns, TurnManager turnManager) {
        //den zug beenden
        // teile dem vorherigen zu, dass der zug beebdet wurde
        // teile dem nächsten spieler die truns zu
        if (NetworkManager.isServer(networkManager) && turnManager != null) {
            TurnManager.broadcastTurnFinished(player, networkManager);
            turnManager.gameStateNextTurn(futureTurns);
        }
    }

    public static void cardHasBeenPulled(Player player, Card card, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager) {
        player.setPlayerTurns(player.getPlayerTurns() - 1);
        if (card instanceof BombCard) {
            ((BombCard) card).handleActions(player, networkManager, discardPile, turnManager);
        } else {
            player.getHand().add(card);
            GameManager.sendCardPulled(player.getPlayerId(), card, networkManager);
            GameManager.sendNopeDisabled(networkManager);
            if (player.getPlayerTurns() == 0) {
                finishTurn(player, networkManager, 1, turnManager);
            }
        }
    }

    public static void cardHasBeenGiven(int playerID, NetworkManager networkManager, Card card) {
        if (card != null) {
            GameManager.sendCardTo(playerID, networkManager, card);
        }
    }

    public static void increaseCatCounter(Player player, Card card) {
        if (card instanceof CatOneCard) {
            player.increaseCatOneCounter();
        } else if (card instanceof CatTwoCard) {
            player.increaseCatTwoCounter();
        } else if (card instanceof CatThreeCard) {
            player.increaseCatThreeCounter();
        } else if (card instanceof CatFourCard) {
            player.increaseCatFourCounter();
        } else if (card instanceof CatFiveCard) {
            player.increaseCatFiveCounter();
        }
    }

    public static void addCardToHand(Player player, NetworkManager networkManager, String cardID) {
        player.addCardToHand(cardID);
        player.updateHandVisually();
        GameManager.updatePlayerHand(player.getPlayerId(), networkManager, player.handToString());
    }
}