package com.example.se2_exploding_kittens.cards;

import com.example.se2_exploding_kittens.R;

public class DefuseCard extends Cards {

    private static int imageResource = R.drawable.defusecard;
    public DefuseCard() {
    }

    @Override
    public int getImageResource() {
        return imageResource;
    }

    @Override
    public void cardAction(Player p1, Player p2) {
     if (isBomb()) {
         setBomb(false);
     }
    }
}
