package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class DefuseCard implements Card {

    public static final int DEFUSE_CARD_ID = 8;

    public DefuseCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.defusecard;
    }

    @Override
    public int getCardID() {
        return DEFUSE_CARD_ID;
    }
}
