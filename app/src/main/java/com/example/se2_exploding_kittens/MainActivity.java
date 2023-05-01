package com.example.se2_exploding_kittens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.se2_exploding_kittens.cards.Deck;

public class MainActivity extends AppCompatActivity {
    Button button;
    Deck deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lb = new LobbyBroadcaster("L1", 45000);
        //ll = new LobbyListener(this);
        Thread broadcast = new Thread(lb);
        broadcast.start();
        //Thread listener = new Thread(ll);
        //listener.start();
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            deck.test();
        });
    }

}