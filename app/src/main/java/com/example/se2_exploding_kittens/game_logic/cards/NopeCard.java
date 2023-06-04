package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class NopeCard implements Card {

    public static final int NOPE_CARD_ID = 10;

    public NopeCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.nopecard;
    }

    @Override
    public int getCardID() {
        return NOPE_CARD_ID;
    }
}
