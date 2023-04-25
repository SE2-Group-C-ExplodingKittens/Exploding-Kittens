package com.example.se2_exploding_kittens.cards;

public class BombCard extends Cards{
    public BombCard(int imageResource) {
        super(imageResource);
    }

    @Override
    public void cardAction(Player p1, Player p2) {
        setBomb(true);

    }
}
