package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class CatThreeCard implements Card {

    public static final int CAT_THREE_CARD_ID = 6;

    @Override
    public int getImageResource() {
        return R.drawable.uglykittencard;
    }

    @Override
    public int getCardID() {
        return CAT_THREE_CARD_ID;
    }

    public CatThreeCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }
}
