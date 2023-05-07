package com.example.se2_exploding_kittens;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

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

public class MainActivity extends AppCompatActivity implements MessageCallback {
    private RecyclerView recyclerView;
    private ArrayList<Cards> cardList;
    private CardAdapter adapter;
    private LobbyBroadcaster lb;
    private NetworkManager connection;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lb = new LobbyBroadcaster("L1", 45000);
        connection = NetworkManager.getInstance();
        //connection.runAsServer(45000);
        //connection.subscribeCallbackToMessageID(this,TEST_MESSAGE_ID);
        //listener.start();
        //ll = new LobbyListener(this);
        Thread broadcast = new Thread(lb);
        broadcast.start();

        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);





        // Implement onDragListener for the discard pile view
        View discardPileView = findViewById(R.id.discardPile);
        discardPileView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DROP:
                        // Get the dragged item from the ClipData object
                        ClipData.Item item =  event.getClipData().getItemAt(0);
                        String cardResourceString = String.valueOf(item.getText());
                        int cardResource =  Integer.parseInt(cardResourceString);
                        // Add the card to the discard pile
                        ImageView discardedCard = new ImageView(MainActivity.this);
                        discardedCard.setImageResource(cardResource);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        discardedCard.setLayoutParams(params);
                        ((ViewGroup) v).addView(discardedCard);
                        // Setting image at the beginning to the invisible state
                        ImageView discardImage = findViewById(R.id.discard_pile_image);
                        discardImage.setVisibility(View.INVISIBLE);

                }
                return true;
            }
        });

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

        cardList.add(new FavorCard());
        cardList.add(new SkipCard());


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
                connection.sendMessageFromTheSever(new Message(MessageType.MESSAGE,TEST_MESSAGE_ID,"Pong"),(ServerTCPSocket)sender);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                try {
                    connection.sendMessageBroadcast(new Message(MessageType.ERROR,TEST_MESSAGE_ID,"Pong Failed"));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                    Log.e("MainActivity", "is not server");
                }

            }
        }

    }
}
