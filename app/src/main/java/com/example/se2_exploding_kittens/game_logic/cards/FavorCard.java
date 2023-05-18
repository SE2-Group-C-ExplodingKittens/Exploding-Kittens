package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class FavorCard implements Card {

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
}
