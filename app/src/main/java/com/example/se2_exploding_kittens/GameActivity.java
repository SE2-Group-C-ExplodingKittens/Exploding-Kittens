package com.example.se2_exploding_kittens;

import static com.example.se2_exploding_kittens.NetworkManager.TEST_MESSAGE_ID;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ClientTCP;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.Card;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements MessageCallback {

    private static final int GAME_ACTIVITY_MESSAGE_ID = 1000;
    private static final int GAME_ACTIVITY_DECK_MESSAGE_ID = 1001;


    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private NetworkManager connection;
    private PlayerManager playerManager = PlayerManager.getInstance();

    private ArrayList<Player> players = new ArrayList<Player>();

    private Deck deck;

    //hand over the player that plays over on the device
    private void guiInit(Player currentPlayer){
        // Implement onDragListener for the discard pile view
        View discardPileView = findViewById(R.id.discardPile);

        Rect dropBounds = new Rect(discardPileView.getLeft(), discardPileView.getTop(),
                discardPileView.getRight(), discardPileView.getBottom());

        discardPileView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Save the original position of the card
                        view.setTag(view.getY());
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        // Add any visual cues for when the card is over the drop area
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        // Check if the card is outside the bounds of the drop area
                        if (!dropBounds.contains((int) event.getX(), (int) event.getY())) {
                            // Move the card back to its original position
                            view.setY((Float) view.getTag());
                        }
                        break;
                    case DragEvent.ACTION_DROP:
                        // Get the dragged item from the ClipData object
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        ClipData.Item mPositionItem = event.getClipData().getItemAt(1);
                        String mPositionString = String.valueOf(mPositionItem.getText());
                        String cardResourceString = String.valueOf(item.getText());
                        int cardResource = Integer.parseInt(cardResourceString);
                        int mPosition = Integer.parseInt(mPositionString);
                        // Add the card to the discard pile
                        ImageView discardedCard = new ImageView(GameActivity.this);
                        discardedCard.setImageResource(cardResource);
                        adapter.removeItem(mPosition);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        discardedCard.setLayoutParams(params);
                        ((ViewGroup) view).addView(discardedCard);
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


        // Initialize the card adapter (for players hand)
        adapter = new CardAdapter(currentPlayer.getHand());

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);

        //Adding the functionality for user to draw a card
        ImageView deckImage = findViewById(R.id.playingDeck);
        deckImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the next card from the deck
                try {
                    Card nextCard = deck.getNextCard();

                    // TODO implement the logic, to process Bomb card differently

                    // Add the next card to the current player's hand
                    currentPlayer.getHand().add(nextCard);

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    Toast.makeText(GameActivity.this, "The deck is empty!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void distributeDeck(Deck deck) {
        try {
            if(deck != null){
                connection.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_ACTIVITY_DECK_MESSAGE_ID, deck.toString()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Free resources
        if (connection != null) {
            if (connection.getConnectionRole() != TypeOfConnectionRole.IDLE) {
                connection.terminateConnection();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        connection = NetworkManager.getInstance();
        long seed = System.currentTimeMillis();

        if(connection.getConnectionRole() == TypeOfConnectionRole.SERVER){
            deck = new Deck(seed);
            playerManager.initializeAsHost(connection.getServerConnections(),connection);
            for (PlayerConnection pc: playerManager.getPlayers()) {
                players.add(pc.getPlayer());
            }
            deck.dealCards(players);
            distributeDeck(deck);



        }else if(connection.getConnectionRole() == TypeOfConnectionRole.CLIENT){
            Player localClientPlayer = new Player();
            playerManager.initializeAsClient(localClientPlayer,connection);
            while (localClientPlayer.getPlayerId() == -1){
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            Toast toast = Toast.makeText(this, "You're Player"+localClientPlayer.getPlayerId(), Toast.LENGTH_LONG);
            toast.setDuration(Toast.LENGTH_SHORT); // 3 seconds
            toast.setGravity(Gravity.BOTTOM, 0, 100); // Display at the bottom with an offset
            toast.show();

            guiInit(localClientPlayer);
        }else if(connection.getConnectionRole() == TypeOfConnectionRole.IDLE){
            //this case just for local testing presumably no connection has ever been established
            //Add players to the player's list
            deck = new Deck(seed);
            Player p1 = new Player(1);
            Player p2 = new Player(2);
            Player p3 = new Player(3);
            Player p4 = new Player(4);
            Player p5 = new Player(5);
            Player p6 = new Player(6);
            players.add(p1);
            players.add(p2);
            players.add(p3);
            players.add(p4);
            players.add(p5);
            players.add(p6);

            // Deal cards
            deck.dealCards(players);




            guiInit(p1);
        }






    }


    @Override
    public void responseReceived(String text, Object sender) {
        Log.v("GameActivity", text);
        if (sender instanceof ServerTCPSocket) {
            Log.v("GameActivity", "srv");
            try {
                connection.sendMessageFromTheSever(new Message(MessageType.MESSAGE, TEST_MESSAGE_ID, "Pong"), (ServerTCPSocket) sender);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                try {
                    connection.sendMessageBroadcast(new Message(MessageType.ERROR, TEST_MESSAGE_ID, "Pong Failed"));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                    Log.e("GameActivity", "is not server");
                }

            }
        }

        if(sender instanceof ClientTCP){
            switch (Message.parseAndExtractMessageID(text)){
                case GAME_ACTIVITY_DECK_MESSAGE_ID:
                    deck = new Deck(Message.parseAndExtractPayload(text));
                    break;
            }
        }

    }
}
