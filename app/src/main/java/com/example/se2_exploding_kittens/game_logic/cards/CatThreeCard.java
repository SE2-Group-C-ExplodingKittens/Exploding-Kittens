package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;

public class CatThreeCard implements Card {

    public static final int CAT_THREE_CARD_ID = 6;

    @Override
    public int getImageResource() {
        return R.drawable.apple;
    }

    @Override
    public int getCardID() {
        return CAT_THREE_CARD_ID;
    }

    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager) {

    }

    public CatThreeCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }
}
