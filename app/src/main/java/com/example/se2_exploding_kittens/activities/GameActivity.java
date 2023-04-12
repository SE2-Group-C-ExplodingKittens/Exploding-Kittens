package com.example.se2_exploding_kittens.activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se2_exploding_kittens.GameBoard.GameBoard;
import com.example.se2_exploding_kittens.R;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GameBoard gameBoard = new GameBoard();

        Button buttonStart = findViewById(R.id.btn_game_deck);
        buttonStart.setText(String.valueOf(gameBoard.deck.getDeckSize()));
    }


}
