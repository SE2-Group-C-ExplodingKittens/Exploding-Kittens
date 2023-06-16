package com.example.se2_exploding_kittens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button joinGameButton = findViewById(R.id.join_game_button);
        Button hostGameButton = findViewById(R.id.host_game_button);
        Button btnTestGame = findViewById(R.id.gameActivityTest);

        joinGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JoinGameActivity.class);
            startActivity(intent);
        });

        hostGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HostGameActivity.class);
            startActivity(intent);
        });

        btnTestGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        //Testing only

    }
}