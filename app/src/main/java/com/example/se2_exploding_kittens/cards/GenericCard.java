package com.example.se2_exploding_kittens.cards;

public class GenericCard extends AbstractCard {

    public GenericCard(int number) {
        type = "GenericType";
        description = "This is a generic description.";
        this.number = number;
    }

}
