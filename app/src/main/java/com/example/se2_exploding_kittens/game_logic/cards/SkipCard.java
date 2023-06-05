package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;

public class SkipCard implements Card {

    public static final int SKIP_CARD_ID = 13;

    public SkipCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.skipcard;
    }

    @Override
    public int getCardID() {
        return SKIP_CARD_ID;
    }

    public void handleSkipActions(Player player, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager){
        if(player != null){
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            player.setPlayerTurns(player.getPlayerTurns()-1);
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(SKIP_CARD_ID));
            if(player.getPlayerTurns() == 0){
                GameLogic.finishTurn(player,networkManager,1, turnManager);
                GameManager.sendNopeEnabled(networkManager);
            }
        }
        discardPile.putCard(this);
    }

}
