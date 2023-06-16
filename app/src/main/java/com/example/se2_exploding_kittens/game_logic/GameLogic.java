package com.example.se2_exploding_kittens.game_logic;

import android.content.Context;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
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
import java.util.Arrays;

public class GameLogic {

    public static boolean nopeEnabled = false;
    ArrayList<Player> playerList = new ArrayList<>();
    private static final ArrayList<String> playerIDList = new ArrayList<>();
    int idOfLocalPlayer;
    Deck deck;
    int currentPlayer = 0;

    public GameLogic(int numOfPlayers, int idOfLocalPlayer, Deck deck) {
        initPlayers(numOfPlayers);
        this.idOfLocalPlayer = idOfLocalPlayer;
        this.deck = deck;
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
                    if (card instanceof ShuffleCard) {
                        return true;
                    }
                    if (card instanceof AttackCard) {
                        return true;
                    }
                    if (card instanceof SeeTheFutureCard) {
                        return true;
                    }
                    if (card instanceof FavorCard){
                        return true;
                    }
                }else if(nopeEnabled && card instanceof NopeCard){
                    return true;
                }
            }
        }

        return false;
    }

    public static void cardHasBeenPlayed(Player player, Card card, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager, Deck deck, Context context) {
        if(card instanceof SkipCard){
            ((SkipCard) card).handleActions(player,networkManager,discardPile,turnManager);
        }else if(card instanceof DefuseCard){
            ((DefuseCard) card).handleActions(player,networkManager,discardPile,turnManager, deck);
        } else if (card instanceof ShuffleCard) {
            ((ShuffleCard) card).handleActions(player, networkManager, discardPile, deck);
        } else if (card instanceof AttackCard) {
            ((AttackCard) card).handleActions(player, networkManager, discardPile, turnManager);
        } else if (card instanceof SeeTheFutureCard) {
            ((SeeTheFutureCard) card).handleActions(player, networkManager, discardPile, deck, context);
        } else if (card instanceof FavorCard) {
            ((FavorCard) card).handleActions(player, networkManager, discardPile, context);
        } else {
            if (player != null) {
                GameManager.sendCardPlayed(player.getPlayerId(), card, networkManager);
            }
        }
    }

    public static boolean canCardBePulled(Player player){
        if(!player.isAlive()){
            return false;
        }
        return player.getPlayerTurns() > 0;
    }

    public static int checkForWinner(PlayerManager playerManager){
        if(playerManager.getPlayerSize() == 1){
            return playerManager.getPlayers().get(0).getPlayerID();
        }else{
            int alivePlayers = 0;
            int winner = -1;
            for (PlayerConnection pc: playerManager.getPlayers()) {
                Player p = pc.getPlayer();
                if(p.isAlive()){
                    winner = p.getPlayerId();
                    alivePlayers++;
                }
            }
            if (alivePlayers == 1){
                return winner;
            }
        }
        return -1;
    }

    public static void finishTurn(Player player, NetworkManager networkManager, int futureTurns, TurnManager turnManager){
        //den zug beenden
        // teile dem vorherigen zu, dass der zug beebdet wurde
        // teile dem nächsten spieler die truns zu
        if(NetworkManager.isServer(networkManager) && turnManager != null){
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
            if (player.getPlayerTurns() == 0) {
                finishTurn(player, networkManager, 1, turnManager);
            }
        }
    }

    public static void cardHasBeenGiven(int playerID, NetworkManager networkManager, Card card) {
        if (card != null) {
            GameManager.sendCardGiven(playerID, networkManager, card);
        }
    }
}