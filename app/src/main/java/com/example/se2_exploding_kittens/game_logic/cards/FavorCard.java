package com.example.se2_exploding_kittens.game_logic.cards;

import android.content.Context;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;

public class FavorCard extends ChoosePlayerCard {

    public static final int FAVOR_CARD_ID = 9;

    public FavorCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.favorcard;
    }

    @Override
    public int getCardID() {
        return FAVOR_CARD_ID;
    }

    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, Context context) {
        if (player != null) {
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            if (context != null) {
                //context is null if the server broadcasts this method
                //it has to be null in order not to call this method

                //so we can get the connection when the button gets (not) pressed
                setNetworkManager(networkManager);
                setPlayerID(player.getPlayerId());
                showChoosePlayerLayout(player.getPlayerId(), networkManager, context);
            }
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(FAVOR_CARD_ID));
        }
        discardPile.putCard(this);
    }
}
