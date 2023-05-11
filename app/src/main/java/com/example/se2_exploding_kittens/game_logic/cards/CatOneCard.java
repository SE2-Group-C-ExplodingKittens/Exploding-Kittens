package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.R;

public class CatOneCard extends Card {
    @Override
    public int getImageResource() {
        return R.drawable.uglykittencard;
    }

    public CatOneCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }
}
