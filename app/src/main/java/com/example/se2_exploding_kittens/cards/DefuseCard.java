package com.example.se2_exploding_kittens.cards;

import com.example.se2_exploding_kittens.R;

public class DefuseCard extends Cards {

    private static int imageResource = R.drawable.defusecard;
    private static String cardName = "DefuseCard";
    public DefuseCard() {
    }

    @Override
    public int getImageResource() {
        return imageResource;
    }

    public static String getCardName() {
        return cardName;
    }

    @Override
    public void cardAction(Player p1, Player p2) {
     if (isBomb()) {
         setBomb(false);
     }
    }
}
