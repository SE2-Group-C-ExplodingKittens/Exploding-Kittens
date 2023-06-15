package com.example.se2_exploding_kittens.game_logic;

import android.content.Context;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.cards.AttackCard;
import com.example.se2_exploding_kittens.game_logic.cards.BombCard;
import com.example.se2_exploding_kittens.game_logic.cards.Card;
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

    public static void setPlayerIDList(String text) {
        String[] playerList = text.split(":");
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
        if (player.getPlayerTurns() > 0 || nopeEnabled && card instanceof NopeCard) {
            if (player.getPlayerTurns() > 0) {
                //TODO some cards cant be played, like defuse if no bomb has been pulled
                if (player.isHasBomb() && card instanceof DefuseCard) {
                    return true;
                }
                if (card instanceof SkipCard) {
                    return true;
                }
                if (card instanceof ShuffleCard) {
                    return true;
                }
                if (card instanceof AttackCard) {
                    return true;
                }
                if (card instanceof SeeTheFutureCard) {
                    return true;
                }
                if (card instanceof FavorCard) {
                    return true;
                }
            } else if (nopeEnabled && card instanceof NopeCard) {
                return true;
            }
        }
        return false;
    }

    public static void cardHasBeenPlayed(Player player, Card card, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager, Deck deck, Context context) {
        if (card instanceof SkipCard) {
            ((SkipCard) card).handleSkipActions(player, networkManager, discardPile, turnManager);
        } else if (card instanceof ShuffleCard) {
            ((ShuffleCard) card).handleShuffleActions(player, networkManager, discardPile, deck);
        } else if (card instanceof AttackCard) {
            ((AttackCard) card).handleAttackActions(player, networkManager, discardPile, turnManager);
        } else if (card instanceof SeeTheFutureCard) {
            ((SeeTheFutureCard) card).handleFutureActions(player, networkManager, discardPile, deck, context);
        } else if (card instanceof FavorCard) {
            ((FavorCard) card).handleFavorActions(player, networkManager, discardPile, context);
        } else {
            if (player != null) {
                GameManager.sendCardPlayed(player.getPlayerId(), card, networkManager);
            }
        }
    }

    public static boolean canCardBePulled(Player player) {
        if (player.getPlayerTurns() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void finishTurn(Player player, NetworkManager networkManager, int futureTurns, TurnManager turnManager) {
        if (networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER && turnManager != null) {
            TurnManager.broadcastTurnFinished(player, networkManager);
            turnManager.gameStateNextTurn(futureTurns);
        }
    }

    public static void cardHasBeenPulled(Player player, Card card, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager) {
        player.setPlayerTurns(player.getPlayerTurns() - 1);
        if (card instanceof BombCard) {
            player.setHasBomb(true);
            GameManager.sendBombPulled(player.getPlayerId(), card, networkManager);
            discardPile.putCard(card);

        } else {
            player.getHand().add(card);
            GameManager.sendCardPulled(player.getPlayerId(), card, networkManager);
            GameManager.sendNopeDisabled(networkManager);
            if (player.getPlayerTurns() == 0) {
                finishTurn(player, networkManager, 1, turnManager);
            }
        }
    }

    public static void cardHasBeenGiven(int playerID, NetworkManager networkManager, Card card){
        GameManager.sendCardGiven(playerID, networkManager, card);
    }
}