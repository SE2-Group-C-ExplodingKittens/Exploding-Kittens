package com.example.se2_exploding_kittens.gameLogik.cards;

import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.gameLogik.Player;

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
