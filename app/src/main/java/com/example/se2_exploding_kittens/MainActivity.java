package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.NetworkManager.TEST_MESSAGE_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.example.se2_exploding_kittens.Network.LobbyLogic.LobbyBroadcaster;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.cards.AttackCard;
import com.example.se2_exploding_kittens.cards.Cards;
import com.example.se2_exploding_kittens.cards.DefuseCard;
import com.example.se2_exploding_kittens.cards.FavorCard;
import com.example.se2_exploding_kittens.cards.NopeCard;
import com.example.se2_exploding_kittens.cards.SkipCard;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

public class MainActivity extends AppCompatActivity implements MessageCallback {
    private RecyclerView recyclerView;
    private ArrayList<Cards> cardList;
    private CardAdapter adapter;
    private LobbyBroadcaster lb;
    private NetworkManager server;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lb = new LobbyBroadcaster("L1", 45000);
        server = new NetworkManager();
        server.runAsServer(45000);
        server.subscribeCallbackToMessageID(this,TEST_MESSAGE_ID);
        //listener.start();
        //ll = new LobbyListener(this);
        Thread broadcast = new Thread(lb);
        broadcast.start();
        //Thread listener = new Thread(ll);
        //listener.start();
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);





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
        cardList.add(new DefuseCard(R.drawable.defusecard));
        cardList.add(new NopeCard(R.drawable.nopecard));
        cardList.add(new SkipCard(R.drawable.skipcard));
        cardList.add(new AttackCard(R.drawable.attackcard));
        cardList.add(new FavorCard(R.drawable.favorcard));
        cardList.add(new SkipCard(R.drawable.skipcard));
        cardList.add(new AttackCard(R.drawable.attackcard));
        cardList.add(new FavorCard(R.drawable.favorcard));
        cardList.add(new SkipCard(R.drawable.skipcard));
        cardList.add(new AttackCard(R.drawable.attackcard));
        cardList.add(new FavorCard(R.drawable.favorcard));
        cardList.add(new SkipCard(R.drawable.skipcard));
        cardList.add(new AttackCard(R.drawable.attackcard));
        // Add more cards as needed


        adapter = new CardAdapter(cardList);

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void responseReceived(String text, Object sender) {
        Log.v("MainActivity", text);
        if(sender instanceof ServerTCPSocket){
            Log.v("MainActivity", "srv");
            try{
                server.sendMessageFromTheSever(new Message(MessageType.MESSAGE,TEST_MESSAGE_ID,"Pong"),(ServerTCPSocket)sender);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                try {
                    server.sendMessageBroadcast(new Message(MessageType.ERROR,TEST_MESSAGE_ID,"Pong Failed"));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                    Log.e("MainActivity", "is not server");
                }

            }
        }

    }
}
