package com.example.se2_exploding_kittens.cards;

public class DefuseCard extends Cards {

    int imageResource;
    public DefuseCard(int imageResource) {

        super(imageResource);
        this.imageResource = imageResource;
    }

    @Override
    public int getImageResource() {
        return imageResource;
    }

    @Override
    public void cardAction(Player p1, Player p2) {
     if (isBomb()) {
         setBomb(false);
     }
    }
}
