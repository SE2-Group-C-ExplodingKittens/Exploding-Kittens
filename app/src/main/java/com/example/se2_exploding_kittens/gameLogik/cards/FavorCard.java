package com.example.se2_exploding_kittens.gameLogik.cards;

import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.gameLogik.Player;

public class FavorCard extends Cards {
    public FavorCard() {

    }

    private static int imageResource = R.drawable.favorcard;
    private static String cardName = "FavorCard";
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
