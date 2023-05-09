package com.example.se2_exploding_kittens.gameLogik.cards;

import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.gameLogik.Player;

public class SeeTheFutureCard extends Cards {
    public SeeTheFutureCard() {
    }

    private static int imageResource = R.drawable.seethefuturecard;
    private static String cardName = "SEETHEFUTURECARD";
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
