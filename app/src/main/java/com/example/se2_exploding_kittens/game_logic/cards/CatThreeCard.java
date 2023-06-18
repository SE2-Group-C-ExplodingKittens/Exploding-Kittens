package com.example.se2_exploding_kittens.game_logic.cards;

import android.content.Context;
import android.widget.Button;

import com.example.se2_exploding_kittens.Activities.GameActivity;
import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;

public class CatThreeCard extends ChoosePlayerCard {

    public static final int CAT_THREE_CARD_ID = 6;

    public CatThreeCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.apple;
    }

    @Override
    public int getCardID() {
        return CAT_THREE_CARD_ID;
    }

    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, Context context) {
        if (player != null) {
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            GameLogic.increaseCatCounter(player, this);
            if (context != null) {
                GameActivity gameActivity = (GameActivity) context;
                Button buttonTwoCats = gameActivity.findViewById(R.id.buttonTwoCats);
                Button buttonThreeCats = gameActivity.findViewById(R.id.buttonThreeCats);
                checkCounterAndSetupButtons(buttonTwoCats, buttonThreeCats, player, networkManager, context, discardPile, this);
            }
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(CAT_THREE_CARD_ID));
            GameManager.sendNopeEnabled(networkManager);
        }
        discardPile.putCard(this);
    }
}
