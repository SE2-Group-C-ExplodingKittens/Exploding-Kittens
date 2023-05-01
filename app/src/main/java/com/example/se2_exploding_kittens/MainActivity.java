package com.example.se2_exploding_kittens;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.Network.LobbyBroadcaster;
import com.example.se2_exploding_kittens.cards.AttackCard;
import com.example.se2_exploding_kittens.cards.Cards;
import com.example.se2_exploding_kittens.cards.DefuseCard;
import com.example.se2_exploding_kittens.cards.NopeCard;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Cards> cardList;
    private CardAdapter adapter;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LobbyBroadcaster lb = new LobbyBroadcaster("L1", 45000);
        //ll = new LobbyListener(this);
        Thread broadcast = new Thread(lb);
        broadcast.start();
        //Thread listener = new Thread(ll);
        //listener.start();
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView and layout manager
        recyclerView = findViewById(R.id.recyclerVw);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // This lines of code make sure, that cards are displayed overlapping each other
        int horizontalOverlapPx = getResources().getDimensionPixelSize(R.dimen.card_horizontal_overlap);
        int startMarginPx = getResources().getDimensionPixelSize(R.dimen.card_start_margin);
        int verticalOffset = getResources().getDimensionPixelSize(R.dimen.card_vertical_offset);
        recyclerView.addItemDecoration(new OverlapDecoration(horizontalOverlapPx, startMarginPx, verticalOffset));

        // Initialize the list of cards and the adapter
        cardList = new ArrayList<Cards>();
        cardList.add(new AttackCard());
        cardList.add(new DefuseCard());
        cardList.add(new NopeCard());


        // Add more cards as needed


        adapter = new CardAdapter(cardList);

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);


    }
}
