package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;

public class BombCard implements Card {

    public static final int BOMB_CARD_ID = 0;

    public BombCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.explodingkittencard;
    }

    @Override
    public int getCardID() {
        return BOMB_CARD_ID;
    }

    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager){
        //wenn eine bombe gezogen wird,
        //1 setze bombe bei spieler
        //2 deaktiviere nope
        //3 anderen mitteilen und karte setzen
        //4 überprüfen ob defuse existiert, wenn nicht -> ausscheiden
        discardPile.putCard(this);
        if (player != null) {
            player.setHasBomb(true);
            GameManager.sendNopeDisabled(networkManager);
            GameManager.sendBombPulled(player.getPlayerId(), this, networkManager);
            if(player.getDefuse() == -1){
                //no defuse existing player has lost
                player.setAlive(false);
                if(NetworkManager.isServer(networkManager)){
                    GameManager.sendPlayerLost(player.getPlayerId(),networkManager);
                    GameLogic.finishTurn(player,networkManager,1, turnManager);
                }
            }
        }
    }

}
