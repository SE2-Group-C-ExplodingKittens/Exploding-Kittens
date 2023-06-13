package com.example.se2_exploding_kittens;

import android.content.ClipData;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ClientTCP;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameClient;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.Card;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements MessageCallback {

    private static final int GAME_ACTIVITY_MESSAGE_ID = 1000;
    public static final int GAME_ACTIVITY_DECK_MESSAGE_ID = 1001;
    public static final int GAME_ACTIVITY_SHOW_THREE_CARDS_ID = 1002;

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private NetworkManager connection;
    private PlayerManager playerManager = PlayerManager.getInstance();
    private TextView yourTurnTextView;
    private TextView seeTheFutureCardTextView;

    private ImageView deckImage;
    private GameManager gameManager;
    private Player localClientPlayer;

    private Deck deck;
    private DiscardPile discardPile;
    private GameClient gameClient;

    private View discardPileView;

    PropertyChangeListener cardPileChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ("discardPile".equals(event.getPropertyName())) {
                        //ImageView discardImage = findViewById(R.id.discard_pile_image);
                        //discardImage.setImageResource(discardPile.getCardPile().get(0).getImageResource());

                        ImageView discardedCard = new ImageView(GameActivity.this);
                        discardedCard.setImageResource(discardPile.getCardPile().get(0).getImageResource());

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        discardedCard.setLayoutParams(params);
                        ((ViewGroup) discardPileView).addView(discardedCard);
                        // Setting image at the beginning to the invisible state
                        ImageView discardImage = findViewById(R.id.discard_pile_image);
                        discardImage.setVisibility(View.INVISIBLE);
                    }
                }
            });

        }
    };

    PropertyChangeListener yourTurnListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (evt.getNewValue() instanceof Integer) {
                        if ("yourTurn".equals(evt.getPropertyName())) {
                            //check if the local player caused this event
                            if (connection.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                                if (playerManager.getLocalSelf().getPlayerId() == (int) evt.getNewValue()) {
                                    yourTurnTextView.setVisibility(View.VISIBLE);
                                }
                            } else if (connection.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                                if (localClientPlayer.getPlayerId() == (int) evt.getNewValue()) {
                                    yourTurnTextView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            });
        }
    };

    PropertyChangeListener notYourTurnListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (evt.getNewValue() instanceof Integer) {
                        if ("notYourTurn".equals(evt.getPropertyName())) {
                            //check if the local player caused this event
                            if (connection.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                                if (playerManager.getLocalSelf().getPlayerId() == (int) evt.getNewValue()) {
                                    yourTurnTextView.setVisibility(View.INVISIBLE);
                                }
                            } else if (connection.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
                                if (localClientPlayer.getPlayerId() == (int) evt.getNewValue()) {
                                    yourTurnTextView.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                }
            });
        }
    };


    //hand over the player that plays over on the device
    private void guiInit(Player currentPlayer) {
        // Implement onDragListener for the discard pile view
        discardPileView = findViewById(R.id.discardPile);

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
/*                        ImageView discardedCard = new ImageView(GameActivity.this);
                        discardedCard.setImageResource(cardResource);*/

                        Card selectedCard = adapter.getSelectedCard(mPosition);
                        if (GameLogic.canCardBePlayed(currentPlayer, selectedCard)) {
                            adapter.removeCard(mPosition);
                            if (connection.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                                GameLogic.cardHasBeenPlayed(currentPlayer, selectedCard, connection, discardPile, gameManager.getTurnManage(), deck, GameActivity.this);
                            } else {
                                GameLogic.cardHasBeenPlayed(currentPlayer, selectedCard, connection, discardPile, null, deck, GameActivity.this);
                            }

                            // changed via discard pile porperty changes
/*                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            discardedCard.setLayoutParams(params);
                            ((ViewGroup) view).addView(discardedCard);
                            // Setting image at the beginning to the invisible state
                            ImageView discardImage = findViewById(R.id.discard_pile_image);
                            discardImage.setVisibility(View.INVISIBLE);*/
                        }
                }
                return true;
            }
        });
        discardPile.addPropertyChangeListener(cardPileChangeListener);
        playerHandInit(currentPlayer);
    }

    private void playerHandInit(Player currentPlayer) {
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
        currentPlayer.addPropertyChangeListener(adapter);
        //currentPlayer-dataSet may change async before addPropertyChangeListener is registered
        adapter.notifyDataSetChanged();

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);

        //Adding the functionality for user to draw a card
        deckImage = findViewById(R.id.playingDeck);
        deckImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the next card from the deck
                try {
                    if (GameLogic.canCardBePulled(currentPlayer)) {
                        Card nextCard = deck.getNextCard();
                        if (connection.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                            GameLogic.cardHasBeenPulled(currentPlayer, nextCard, connection, discardPile, gameManager.getTurnManage());
                        } else {
                            GameLogic.cardHasBeenPulled(currentPlayer, nextCard, connection, discardPile, null);
                        }
                        /*if(nextCard instanceof BombCard){

                        }else {

                        }
                        // TODO implement the logic, to process Bomb card differently

                        // Add the next card to the current player's hand
                        currentPlayer.getHand().add(nextCard);*/

                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    }

                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    Toast.makeText(GameActivity.this, "The deck is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void distributeDeck(Deck deck) {
        try {
            if (deck != null) {
                connection.sendMessageBroadcast(new Message(MessageType.MESSAGE, GAME_ACTIVITY_DECK_MESSAGE_ID, deck.deckToString()));
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
            connection = null;
        }
        if (playerManager != null) {
            playerManager.reset();
            playerManager = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        yourTurnTextView = findViewById(R.id.textViewYourTurn);
        seeTheFutureCardTextView = findViewById(R.id.textViewSeeTheFutureCard);
        connection = NetworkManager.getInstance();
        connection.subscribeCallbackToMessageID(this, GAME_ACTIVITY_DECK_MESSAGE_ID);
        connection.subscribeCallbackToMessageID(this, GAME_ACTIVITY_SHOW_THREE_CARDS_ID);
        long seed = System.currentTimeMillis();
        discardPile = new DiscardPile();

        if (connection.getConnectionRole() == TypeOfConnectionRole.SERVER) {
            deck = new Deck(seed);
            playerManager.initializeAsHost(connection.getServerConnections(), connection);
            ArrayList<Player> players = new ArrayList<Player>();
            for (PlayerConnection pc : playerManager.getPlayers()) {
                players.add(pc.getPlayer());
                pc.getPlayer().subscribePlayerToCardEvents(connection);
            }
            deck.dealCards(players);
            gameManager = new GameManager(connection, deck, discardPile);
            distributeDeck(deck);
            gameManager.distributePlayerHands();

            // player id 0 is always the host
            guiInit(playerManager.getLocalSelf());
            playerManager.getLocalSelf().addPropertyChangeListener(yourTurnListener);
            playerManager.getLocalSelf().addPropertyChangeListener(notYourTurnListener);
            gameManager.startGame();
        } else if (connection.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
            deck = null;
            localClientPlayer = new Player();
            localClientPlayer.subscribePlayerToCardEvents(connection);
            localClientPlayer.addPropertyChangeListener(yourTurnListener);
            localClientPlayer.addPropertyChangeListener(notYourTurnListener);
            //playerManager.initializeAsClient(localClientPlayer,connection);
            //gameManager = new GameManager(connection, null,discardPile);
            gameClient = new GameClient(localClientPlayer, deck, discardPile, connection);

            Toast toast = Toast.makeText(this, "Waiting for host to start", Toast.LENGTH_SHORT);
            toast.setDuration(Toast.LENGTH_SHORT); // 3 seconds
            toast.setGravity(Gravity.BOTTOM, 0, 100); // Display at the bottom with an offset
            toast.show();

            gameClient.blockUntilReady();


            toast = Toast.makeText(this, "You're Player" + localClientPlayer.getPlayerId(), Toast.LENGTH_SHORT);
            toast.setDuration(Toast.LENGTH_SHORT); // 3 seconds
            toast.setGravity(Gravity.BOTTOM, 0, 100); // Display at the bottom with an offset
            toast.show();
            guiInit(localClientPlayer);


        } else if (connection.getConnectionRole() == TypeOfConnectionRole.IDLE) {
            //this case just for local testing presumably no connection has ever been established
            //Add players to the player's list
            ArrayList<Player> players = new ArrayList<Player>();
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
        if (sender instanceof ClientTCP) {
            if (Message.parseAndExtractMessageID(text) == GAME_ACTIVITY_DECK_MESSAGE_ID) {
                deck = new Deck(Message.parseAndExtractPayload(text));
                if (connection.getConnectionRole() == TypeOfConnectionRole.CLIENT && gameClient != null) {
                    gameClient.setDeck(deck);
                }
            }
        }
        if (Message.parseAndExtractMessageID(text) == GAME_ACTIVITY_SHOW_THREE_CARDS_ID) {
            int playerID = Integer.parseInt(Message.parseAndExtractPayload(text));
            // If Client to get localClientPlayer
            if (sender instanceof ClientTCP) {
                if (playerID != localClientPlayer.getPlayerId()) {
                    displaySeeTheFutureCardText(playerID);
                }
                // If Server to get getLocalSelf
            } else if (sender instanceof ServerTCPSocket) {
                if (playerID != playerManager.getLocalSelf().getPlayerId()) {
                    displaySeeTheFutureCardText(playerID);
                }
            }
        }
    }

    private void displaySeeTheFutureCardText(int playerID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String txt = "Player " + playerID + " is watching the top three cards of the deck!";
                seeTheFutureCardTextView.setText(txt);
                seeTheFutureCardTextView.setVisibility(View.VISIBLE);
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // set invisible after 3 seconds
                seeTheFutureCardTextView.setVisibility(View.INVISIBLE);
            }
        }, 3000); // 3000 milliseconds = 3 seconds
    }


}
