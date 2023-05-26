package com.example.se2_exploding_kittens.game_logic;

public enum PlayerMessageID {

    PLAYER_HAND_MESSAGE_ID(1101),
    PLAYER_CARD_ADDED_MESSAGE_ID(1102),
    PLAYER_CARD_REMOVED_MESSAGE_ID(1103);

    public final int id;

    private PlayerMessageID(int id) {
        this.id = id;
    }
}
