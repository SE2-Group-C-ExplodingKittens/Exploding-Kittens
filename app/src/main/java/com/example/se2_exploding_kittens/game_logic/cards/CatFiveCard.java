package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class CatFiveCard implements Card {

    public static final int CAT_FIVE_CARD_ID = 3;

    @Override
    public int getImageResource() {
        return R.drawable.uglykittencard;
    }

    @Override
    public int getCardID() {
        return CAT_FIVE_CARD_ID;
    }

    public CatFiveCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }
}