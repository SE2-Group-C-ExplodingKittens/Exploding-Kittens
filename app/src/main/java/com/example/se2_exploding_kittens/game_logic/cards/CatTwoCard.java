package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class CatTwoCard implements Card {

    public static final int CAT_TWO_CARD_ID = 7;

    @Override
    public int getImageResource() {
        return R.drawable.uglykittencard;
    }

    @Override
    public int getCardID() {
        return CAT_TWO_CARD_ID;
    }

    public CatTwoCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }
}
