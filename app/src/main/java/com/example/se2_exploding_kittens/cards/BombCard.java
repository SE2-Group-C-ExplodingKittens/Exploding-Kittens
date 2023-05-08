package com.example.se2_exploding_kittens.cards;

import com.example.se2_exploding_kittens.R;

public class BombCard extends Cards{
    public BombCard() {

    }
    private static int imageResource = R.drawable.explodingkittencard;
    private static String cardName = "BombCard";
    @Override
    public int getImageResource() {
        return imageResource;
    }

    public static String getCardName() {
        return cardName;
    }
    @Override
    public void cardAction(Player p1, Player p2) {
        setBomb(true);

    }
}
