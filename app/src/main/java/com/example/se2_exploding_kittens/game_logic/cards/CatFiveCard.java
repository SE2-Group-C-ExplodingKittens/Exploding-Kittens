package com.example.se2_exploding_kittens.game_logic.cards;

import android.content.Context;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;

public class CatFiveCard extends ChoosePlayerCard {

    public static final int CAT_FIVE_CARD_ID = 3;

    public CatFiveCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.car;
    }

    @Override
    public int getCardID() {
        return CAT_FIVE_CARD_ID;
    }

    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, Context context) {
        if (player != null) {
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            GameLogic.increaseCatCounter(player, this);
            if (player.isCatCounterThree(this) && context != null) {
                setNetworkManager(networkManager);
                setPlayerID(player.getPlayerId());
                showChoosePlayerLayout(player.getPlayerId(), networkManager, context);
            }
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(CAT_FIVE_CARD_ID));
            GameManager.sendNopeEnabled(networkManager);
        }
        discardPile.putCard(this);
    }
}