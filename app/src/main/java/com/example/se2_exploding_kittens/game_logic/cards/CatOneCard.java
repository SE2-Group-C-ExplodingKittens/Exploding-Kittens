package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;

public class CatOneCard implements Card {

    public static final int CAT_ONE_CARD_ID = 5;

    @Override
    public int getImageResource() {
        return R.drawable.uglykittencard;
    }

    @Override
    public int getCardID() {
        return CAT_ONE_CARD_ID;
    }

    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager) {

    }

    public CatOneCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }
}
