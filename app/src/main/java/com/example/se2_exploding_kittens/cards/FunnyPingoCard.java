package com.example.se2_exploding_kittens.cards;

import com.example.se2_exploding_kittens.R;

public class FunnyPingoCard extends Cards {
    private static int imageResource = R.drawable.funnypingo;
    private static String cardName = "FunnyPingo";
    @Override
    public int getImageResource() {
        return imageResource;
    }

    public static String getCardName() {
        return cardName;
    }
    public FunnyPingoCard() {

    }

    @Override
    public void cardAction(Player p1, Player p2) {

    }
}
