package com.example.se2_exploding_kittens.cards;

import com.example.se2_exploding_kittens.R;

public class SkipCard extends Cards {
    public SkipCard() {
    }

    private static int imageResource = R.drawable.skipcard;
    private static String cardName = "SkipCard";
    @Override
    public int getImageResource() {
        return imageResource;
    }

    public static String getCardName() {
        return cardName;
    }

    @Override
    public void cardAction(Player p1, Player p2) {

    }
}
