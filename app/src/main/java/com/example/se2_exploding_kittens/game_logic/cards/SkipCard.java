package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class SkipCard implements Card {

    public static final int SKIP_CARD_ID = 13;

    public SkipCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.skipcard;
    }

    @Override
    public int getCardID() {
        return SKIP_CARD_ID;
    }
}
