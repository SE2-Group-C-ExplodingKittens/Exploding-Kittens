package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class CatFourCard implements Card {

    public static final int CAT_FOUR_CARD_ID = 4;

    @Override
    public int getImageResource() {
        return R.drawable.uglykittencard;
    }

    @Override
    public int getCardID() {
        return CAT_FOUR_CARD_ID;
    }

    public CatFourCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }
}