package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class AttackCard implements Card {

    public AttackCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.attackcard;
    }
}
