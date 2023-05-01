package com.example.se2_exploding_kittens.cards;

import com.example.se2_exploding_kittens.R;

public class FavorCard extends Cards {
    public FavorCard() {

    }

    private static int imageResource = R.drawable.favorcard;
    @Override
    public int getImageResource() {
        return imageResource;
    }

    @Override
    public void cardAction(Player p1, Player p2) {

    }
}
