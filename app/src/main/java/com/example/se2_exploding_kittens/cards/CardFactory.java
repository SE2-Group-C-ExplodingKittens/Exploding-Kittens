package com.example.se2_exploding_kittens.cards;

public class CardFactory {

    private int imageResource = 2131165410;
    public Cards getCard(String cardType){
        if (cardType == null) {
            return null;
        }

//        TODO change the arguments in card objects
        if (cardType.equalsIgnoreCase("ATTACKCARD")) {
            return new AttackCard(imageResource);
        } else if (cardType.equalsIgnoreCase("CATCARD")) {
            return new BombCard(imageResource);
        } else if (cardType.equalsIgnoreCase("DEFUSECARD")) {
            return new BombCard(imageResource);
        }else if (cardType.equalsIgnoreCase("FAVORCARD")) {
            return new BombCard(imageResource);
        }else if (cardType.equalsIgnoreCase("NOPECARD")) {
            return new BombCard(imageResource);
        }else if (cardType.equalsIgnoreCase("SHUFFLECARD")) {
            return new BombCard(imageResource);
        }else if (cardType.equalsIgnoreCase("SKIPCARD")) {
            return new BombCard(imageResource);
        }else if (cardType.equalsIgnoreCase("BOMBCARD")) {
            return new BombCard(imageResource);
        } return null;
    }
}

