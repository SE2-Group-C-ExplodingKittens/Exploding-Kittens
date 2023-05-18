package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class BombCard implements Card {

    public static final int BOMB_CARD_ID = 2;

    public BombCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.explodingkittencard;
    }

    @Override
    public int getCardID() {
        return BOMB_CARD_ID;
    }
}
