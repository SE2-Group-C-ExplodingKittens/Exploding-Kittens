package com.example.se2_exploding_kittens.cards;

public class AttackCard extends Cards {

    public AttackCard(int imageResource) {
        super(imageResource);
    }
    int imageResource;
    @Override
    public int getImageResource() {
        return imageResource;
    }

    @Override
    public void cardAction(Player p1, Player p2) {

    }
}
