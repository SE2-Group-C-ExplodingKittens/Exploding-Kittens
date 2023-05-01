package com.example.se2_exploding_kittens.cards;

import com.example.se2_exploding_kittens.R;

public class CatCard extends Cards {
    private static int imageResource = R.drawable.uglykittencard;
    @Override
    public int getImageResource() {
        return imageResource;

    }
    public CatCard() {

    }

    @Override
    public void cardAction(Player p1, Player p2) {

    }
}
