package com.example.se2_exploding_kittens.cards;

import com.example.se2_exploding_kittens.R;

public class NopeCard extends Cards{
    public NopeCard() {


    }

    private static int imageResource = R.drawable.nopecard;
    private static String cardName = "NopeCard";
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
