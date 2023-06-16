package com.example.se2_exploding_kittens.Activities;

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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.CardAdapter;
import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.PlayerConnection;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TCP.ClientTCP;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.OverlapDecoration;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameClient;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;
import com.example.se2_exploding_kittens.game_logic.cards.Card;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements MessageCallback {

    public static final int GAME_ACTIVITY_DECK_MESSAGE_ID = 1001;
    public static final int GAME_ACTIVITY_SHOW_THREE_CARDS_ID = 1002;
    public static final int GAME_ACTIVITY_FAVOR_CARD_ID = 1003;


    private CardAdapter adapter;
    private NetworkManager connection;
    private PlayerManager playerManager = PlayerManager.getInstance();
    private TextView yourTurnTextView;
    private TextView seeTheFutureCardTextView;

    private TextView stealRandomCardTextView;

    private ImageView deckImage;
    private FragmentManager fragmentManager;
    private GameManager gameManager;
    private Player localPlayer;

    private Deck deck;
    private DiscardPile discardPile;
    private GameClient gameClient;
    private RecyclerView recyclerView;
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

    PropertyChangeListener playerWonChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ("playerWon".equals(event.getPropertyName())) {
                        //check if the local player caused this event
                        Toast.makeText(GameActivity.this, "You "+event.getNewValue()+" won!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    PropertyChangeListener playerLostChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ("playerLost".equals(event.getPropertyName())) {
                        Toast.makeText(GameActivity.this, "Player "+event.getNewValue()+" lost!", Toast.LENGTH_SHORT).show();
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
                                if (localPlayer.getPlayerId() == (int) evt.getNewValue()) {
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
                                if (localPlayer.getPlayerId() == (int) evt.getNewValue()) {
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
                            //adapter.removeCard(mPosition);
                            //adapter.notifyItemRemoved(mPosition);
                            if(NetworkManager.isServer(connection)){
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
        winLossMessagesInit(currentPlayer);
    }

    private void winLossMessagesInit(Player currentPlayer){
        currentPlayer.addPropertyChangeListener(playerWonChangeListener);
        currentPlayer.addPropertyChangeListener(playerLostChangeListener);
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
                        if(NetworkManager.isServer(connection)){
                            GameLogic.cardHasBeenPulled(currentPlayer, nextCard, connection, discardPile, gameManager.getTurnManage());
                            gameManager.checkGameEnd();
                        } else {
                            GameLogic.cardHasBeenPulled(currentPlayer, nextCard, connection, discardPile, null);
                        }
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
        if(playerManager != null){
            playerManager.reset();
            playerManager = null;
        }
        if (connection != null) {
            if (NetworkManager.isNotIdle(connection)) {
                connection.terminateConnection();
            }
            connection = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        yourTurnTextView = findViewById(R.id.textViewYourTurn);
        seeTheFutureCardTextView = findViewById(R.id.textViewSeeTheFutureCard);
        stealRandomCardTextView = findViewById(R.id.textViewStealRandomCard);
        connection = NetworkManager.getInstance();
        connection.subscribeCallbackToMessageID(this, GAME_ACTIVITY_DECK_MESSAGE_ID);
        connection.subscribeCallbackToMessageID(this, GAME_ACTIVITY_SHOW_THREE_CARDS_ID);
        connection.subscribeCallbackToMessageID(this, GAME_ACTIVITY_FAVOR_CARD_ID);
        long seed = System.currentTimeMillis();
        discardPile = new DiscardPile();

        if(NetworkManager.isServer(connection)){
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
            localPlayer = playerManager.getLocalSelf();
            gameManager.startGame();
        } else if (connection.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
            deck = null;
            localPlayer = new Player();
            localPlayer.subscribePlayerToCardEvents(connection);
            localPlayer.addPropertyChangeListener(yourTurnListener);
            localPlayer.addPropertyChangeListener(notYourTurnListener);
            //playerManager.initializeAsClient(localClientPlayer,connection);
            //gameManager = new GameManager(connection, null,discardPile);
            gameClient = new GameClient(localPlayer, deck, discardPile, connection);

            Toast toast = Toast.makeText(this, "Waiting for host to start", Toast.LENGTH_SHORT);
            toast.setDuration(Toast.LENGTH_SHORT); // 3 seconds
            toast.setGravity(Gravity.BOTTOM, 0, 100); // Display at the bottom with an offset
            toast.show();

            gameClient.blockUntilReady();


            toast = Toast.makeText(this, "You're Player" + localPlayer.getPlayerId(), Toast.LENGTH_SHORT);
            toast.setDuration(Toast.LENGTH_SHORT); // 3 seconds
            toast.setGravity(Gravity.BOTTOM, 0, 100); // Display at the bottom with an offset
            toast.show();
            guiInit(localPlayer);


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
            if (playerID != localPlayer.getPlayerId()) {
                displaySeeTheFutureCardText(playerID);
            }
        }

        if (Message.parseAndExtractMessageID(text) == GAME_ACTIVITY_FAVOR_CARD_ID) {
            String[] message = Message.parseAndExtractPayload(text).split(":");
            int playerID = Integer.parseInt(message[1]);
            if (playerID == localPlayer.getPlayerId()) {
                // steal a random Card and display text
                displayRandomCardGotStolenText();
                Card card = stealRandomCard(localPlayer);
                //send card to stealer
                GameLogic.cardHasBeenGiven(Integer.parseInt(message[0]), connection, card);
            }
        }
    }

    private void displayRandomCardGotStolenText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stealRandomCardTextView.setVisibility(View.VISIBLE);
            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // set invisible after 3 seconds
                stealRandomCardTextView.setVisibility(View.INVISIBLE);
            }
        }, 3000); // 3000 milliseconds = 3 seconds
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

    private Card stealRandomCard(Player player) {
        if (player.getHand().size() == 0) {
            return null;
        }

        Random random = new Random();
        //get random index from hand
        int randomIndex = random.nextInt(player.getHand().size());
        Card card = player.getHand().get(randomIndex);

        //remove card
        player.removeCardFromHand(Integer.toString(card.getCardID()));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //display change
                adapter.notifyDataSetChanged();
            }
        });
        return card;
    }
}


