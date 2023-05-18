package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class SeeTheFutureCard implements Card {

    public static final int SEE_THE_FUTURE_CARD_ID = 11;

    public SeeTheFutureCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.seethefuturecard;
    }

    @Override
    public int getCardID() {
        return SEE_THE_FUTURE_CARD_ID;
    }
}
