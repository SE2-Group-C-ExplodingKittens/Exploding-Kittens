package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class ShuffleCard implements Card {

    public static final int SHUFFLE_CARD_ID = 12;

    public ShuffleCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.shufflecard;
    }

    @Override
    public int getCardID() {
        return SHUFFLE_CARD_ID;
    }
}
