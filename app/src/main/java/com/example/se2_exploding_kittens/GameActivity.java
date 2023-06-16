package com.example.se2_exploding_kittens;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
    public static final int GAME_ACTIVITY_DECK_MESSAGE_ID = 1001;
    public static final int GAME_ACTIVITY_SHOW_THREE_CARDS_ID = 1002;
    private CardAdapter adapter;
    private NetworkManager connection;
    private PlayerManager playerManager = PlayerManager.getInstance();
    private TextView yourTurnTextView;
    private TextView seeTheFutureCardTextView;

    private GameManager gameManager;
    private Player localClientPlayer;

    private Deck deck;
    private DiscardPile discardPile;
    private GameClient gameClient;

    private View discardPileView;

    private Vibrator vibrator;

    PropertyChangeListener cardPileChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            runOnUiThread(() -> {
                if ("discardPile".equals(event.getPropertyName())) {

                    ImageView discardedCard = new ImageView(GameActivity.this);
                    discardedCard.setImageResource(discardPile.getCardPile().get(0).getImageResource());

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    discardedCard.setLayoutParams(params);
                    ((ViewGroup) discardPileView).addView(discardedCard);
                    ImageView discardImage = findViewById(R.id.discard_pile_image);
                    discardImage.setVisibility(View.INVISIBLE);
                }
            });

        }
    };

    PropertyChangeListener yourTurnListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            runOnUiThread(() -> {
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
            });
        }
    };

    PropertyChangeListener notYourTurnListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            runOnUiThread(() -> {
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
            });
        }
    };


    //hand over the player that plays over on the device
    private void guiInit(Player currentPlayer) {
        // Implement onDragListener for the discard pile view
        discardPileView = findViewById(R.id.discardPile);
        discardPileView.setOnDragListener((view, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                if (vibrator.hasVibrator()) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                        50,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                )
                        );
                    } else {
                        vibrator.vibrate(50);
                    }
                }

                Card selectedCard = adapter.getSelectedCard((int) event.getLocalState());
                if (GameLogic.canCardBePlayed(currentPlayer, selectedCard)) {
                    adapter.removeCard((int) event.getLocalState());
                    if (connection.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                        GameLogic.cardHasBeenPlayed(currentPlayer, selectedCard, connection, discardPile, gameManager.getTurnManage(), deck, GameActivity.this);
                    } else {
                        GameLogic.cardHasBeenPlayed(currentPlayer, selectedCard, connection, discardPile, null, deck, GameActivity.this);
                    }
                }
            }
            return true;
        });
        discardPile.addPropertyChangeListener(cardPileChangeListener);

        playerHandInit(currentPlayer);
    }

    private void playerHandInit(Player currentPlayer) {
        // Initialize the RecyclerView and layout manager
        RecyclerView recyclerView = findViewById(R.id.recyclerVw);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // This lines of code make sure, that cards are displayed overlapping each other
        int offset = getResources().getDimensionPixelSize(R.dimen.card_offset);
        recyclerView.addItemDecoration(new OverlapDecoration(offset));


        // Initialize the card adapter (for players hand)
        adapter = new CardAdapter(currentPlayer.getHand());
        currentPlayer.addPropertyChangeListener(adapter);
        //currentPlayer-dataSet may change async before addPropertyChangeListener is registered
        adapter.notifyDataSetChanged();

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);

        //Adding the functionality for user to draw a card
        ImageView deckImage = findViewById(R.id.playingDeck);
        deckImage.setOnClickListener(v -> {
            // Get the next card from the deck
            try {

                if (GameLogic.canCardBePulled(currentPlayer)) {
                    Card nextCard = deck.getNextCard();
                    if (connection.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                        GameLogic.cardHasBeenPulled(currentPlayer, nextCard, connection, discardPile, gameManager.getTurnManage());
                    } else {
                        GameLogic.cardHasBeenPulled(currentPlayer, nextCard, connection, discardPile, null);
                    }
                    // TODO implement the logic, to process Bomb card differently

                    currentPlayer.getHand().add(nextCard);

                    adapter.notifyDataSetChanged();
                }

            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                Toast.makeText(GameActivity.this, "The deck is empty!", Toast.LENGTH_SHORT).show();
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

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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
            ArrayList<Player> players;
            players = new ArrayList<>();
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
            gameClient = new GameClient(localClientPlayer, deck, discardPile, connection);
            localClientPlayer.addPropertyChangeListener(yourTurnListener);
            localClientPlayer.addPropertyChangeListener(notYourTurnListener);
            gameClient = new GameClient(localClientPlayer, deck, discardPile, connection);

            Toast toast = Toast.makeText(this, "Waiting for host to start", Toast.LENGTH_SHORT);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.show();

            gameClient.blockUntilReady();


            toast = Toast.makeText(this, "You're Player" + localClientPlayer.getPlayerId(), Toast.LENGTH_SHORT);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.show();
            guiInit(localClientPlayer);


        } else if (connection.getConnectionRole() == TypeOfConnectionRole.IDLE) {
            //this case just for local testing presumably no connection has ever been established
            //Add players to the player's list
            ArrayList<Player> players = new ArrayList<>();
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
        runOnUiThread(() -> {
            String txt = "Player " + playerID + " is watching the top three cards of the deck!";
            seeTheFutureCardTextView.setText(txt);
            seeTheFutureCardTextView.setVisibility(View.VISIBLE);
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> seeTheFutureCardTextView.setVisibility(View.INVISIBLE), 3000);
    }
}
