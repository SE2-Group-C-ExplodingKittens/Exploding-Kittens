package com.example.se2_exploding_kittens.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se2_exploding_kittens.GameBoard.GameBoard;
import com.example.se2_exploding_kittens.R;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GameBoard gameBoard = new GameBoard();

        Button buttonDeck = findViewById(R.id.btn_game_deck);
        TextView textHand = findViewById(R.id.tv_game_hand);
        Button buttonDiscardPile = findViewById(R.id.btn_game_discard_pile);

        buttonDeck.setText(String.valueOf(gameBoard.deck.printDeck()));
        textHand.setText(String.valueOf(gameBoard.player.printDeck()));
        buttonDiscardPile.setText(String.valueOf(gameBoard.discardPile.printDeck()));

        buttonDeck.setOnClickListener(view -> {
            gameBoard.drawCard();
            buttonDeck.setText(String.valueOf(gameBoard.deck.printDeck()));
            textHand.setText(String.valueOf(gameBoard.player.printDeck()));
        });

        buttonDiscardPile.setOnClickListener(view -> {
            gameBoard.discardCard();
            buttonDiscardPile.setText(String.valueOf(gameBoard.discardPile.printDeck()));
            textHand.setText(String.valueOf(gameBoard.player.printDeck()));
        });
    }


}
