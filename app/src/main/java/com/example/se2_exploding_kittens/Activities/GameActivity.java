package com.example.se2_exploding_kittens.Activities;

import static com.example.se2_exploding_kittens.game_logic.cards.AttackCard.ATTACK_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.BombCard.BOMB_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.DefuseCard.DEFUSE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.FavorCard.FAVOR_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.NopeCard.NOPE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.SeeTheFutureCard.SEE_THE_FUTURE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.ShuffleCard.SHUFFLE_CARD_ID;
import static com.example.se2_exploding_kittens.game_logic.cards.SkipCard.SKIP_CARD_ID;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.CardAdapter;
import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
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

public class GameActivity extends AppCompatActivity implements MessageCallback, CardAdapter.HelpAskListener {
    public static final int GAME_ACTIVITY_DECK_MESSAGE_ID = 1001;
    public static final int GAME_ACTIVITY_SHOW_THREE_CARDS_ID = 1002;
    public static final int GAME_ACTIVITY_STEAL_CARD = 1003;

    private NetworkManager connection;
    private PlayerManager playerManager = PlayerManager.getInstance();
    private TextView yourTurnTextView;
    private TextView seeTheFutureCardTextView;

    private TextView stealRandomCardTextView;
    private GameManager gameManager;
    private Player localPlayer;
    private Deck deck;
    private DiscardPile discardPile;
    private GameClient gameClient;

    private CardAdapter adapter;
    private TextView yourTurnTextView;
    private TextView seeTheFutureCardTextView;
    private TextView stealRandomCardTextView;
    private View discardPileView;
    private Button buttonTwoCats;
    private Button buttonThreeCats;

    private Vibrator vibrator;
    private ConstraintLayout hintLayout;

    PropertyChangeListener cardPileChangeListener = event -> runOnUiThread(() -> {
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

    PropertyChangeListener catButtonsInvisibleListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            runOnUiThread(() -> {
                if ("setCatButtonsInvisible".equals(evt.getPropertyName())) {
                    buttonTwoCats.setVisibility(View.INVISIBLE);
                    buttonThreeCats.setVisibility(View.INVISIBLE);
                }
            });
        }
    };


    PropertyChangeListener playerWonChangeListener = event -> runOnUiThread(() -> {
        if ("playerWon".equals(event.getPropertyName())) {
            //check if the local player caused this event
            Toast.makeText(GameActivity.this, "You " + event.getNewValue() + " won!", Toast.LENGTH_SHORT).show();
        }
    });

    PropertyChangeListener playerLostChangeListener = event -> runOnUiThread(() -> {
        if ("playerLost".equals(event.getPropertyName())) {
            Toast.makeText(GameActivity.this, "Player " + event.getNewValue() + " lost!", Toast.LENGTH_SHORT).show();
        }
    });

    PropertyChangeListener cardStolenListener = event -> runOnUiThread(() -> {
        if ("cardStolen".equals(event.getPropertyName())) {
            runOnUiThread(() -> stealRandomCardTextView.setVisibility(View.VISIBLE));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // set invisible after 3 seconds
                stealRandomCardTextView.setVisibility(View.INVISIBLE);
            }, 3000); // 3000 milliseconds = 3 seconds
        }
    });

    PropertyChangeListener yourTurnListener = event -> runOnUiThread(() -> {
        if (event.getNewValue() instanceof Integer && ("yourTurn".equals(event.getPropertyName()))) {
            //check if the local player caused this event
            if (localPlayer.getPlayerId() == (int) event.getNewValue()) {
                yourTurnTextView.setVisibility(View.VISIBLE);
            }
        }
    });

    PropertyChangeListener notYourTurnListener = event -> runOnUiThread(() -> {
        if (event.getNewValue() instanceof Integer && ("notYourTurn".equals(event.getPropertyName()) && localPlayer.getPlayerId() == (int) event.getNewValue())) {
            //check if the local player caused this event
            yourTurnTextView.setVisibility(View.INVISIBLE);

        }
    });

private void initDiscardPile(DiscardPile discardPile,Player currentPlayer,PropertyChangeListener cardPileChangeListener){
        discardPileView=findViewById(R.id.discardPile);
        discardPileView.setOnDragListener((view,event)->{
        discardPileHandleDropAction(discardPile,currentPlayer,event);
        return true;
        });
        discardPile.addPropertyChangeListener(cardPileChangeListener);
        }

private void discardPileHandleDropAction(DiscardPile discardPile,Player currentPlayer,DragEvent event){
        if(event.getAction()==DragEvent.ACTION_DROP){
        if(vibrator.hasVibrator()){
        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.O){
        vibrator.vibrate(
        VibrationEffect.createOneShot(
        50,
        VibrationEffect.DEFAULT_AMPLITUDE
        )
        );
        }else{
        vibrator.vibrate(50);
        }
        }

        Card selectedCard=adapter.getSelectedCard((int)event.getLocalState());

        // Had to remove, doesn't allow to play card
        if(GameLogic.canCardBePlayed(currentPlayer,selectedCard)){
        if(NetworkManager.isServer(connection)){
        GameLogic.cardHasBeenPlayed(currentPlayer,selectedCard,connection,discardPile,gameManager.getTurnManage(),deck,GameActivity.this);
        }else{
        GameLogic.cardHasBeenPlayed(currentPlayer,selectedCard,connection,discardPile,null,deck,GameActivity.this);
        }
        }
        }
        }

//hand over the player that plays over on the device
private void guiInit(Player currentPlayer){
        // Implement onDragListener for the discard pile view

        initDiscardPile(discardPile,currentPlayer,cardPileChangeListener);

        playerHandInit(currentPlayer);
        winLossMessagesInit(currentPlayer);
        }

private void winLossMessagesInit(Player currentPlayer){
        currentPlayer.addPropertyChangeListener(playerWonChangeListener);
        currentPlayer.addPropertyChangeListener(playerLostChangeListener);
        }

private void playerHandInit(Player currentPlayer){
        // Initialize the RecyclerView and layout manager
        RecyclerView recyclerView=findViewById(R.id.recyclerVw);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        // This lines of code make sure, that cards are displayed overlapping each other
        int offset=getResources().getDimensionPixelSize(R.dimen.card_offset);
        recyclerView.addItemDecoration(new OverlapDecoration(offset));


        // Initialize the card adapter (for players hand)
        adapter=new CardAdapter(currentPlayer.getHand(),this);
        currentPlayer.addPropertyChangeListener(adapter);
        //currentPlayer-dataSet may change async before addPropertyChangeListener is registered
        adapter.notifyDataSetChanged();

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);

        //Adding the functionality for user to draw a card
        ImageView deckImage=findViewById(R.id.playingDeck);
        deckImage.setOnClickListener(v->{
        // Get the next card from the deck
        try{

        if(GameLogic.canCardBePulled(currentPlayer)){
        Card nextCard=deck.getNextCard();
        if(NetworkManager.isServer(connection)){
        GameLogic.cardHasBeenPulled(currentPlayer,nextCard,connection,discardPile,gameManager.getTurnManage());
        gameManager.checkGameEnd();
        }else{
        GameLogic.cardHasBeenPulled(currentPlayer,nextCard,connection,discardPile,null);
        }
        adapter.notifyDataSetChanged();
        }
        }catch(IndexOutOfBoundsException indexOutOfBoundsException){
        Toast.makeText(GameActivity.this,"The deck is empty!",Toast.LENGTH_SHORT).show();
        }
        });
        }


@Override
protected void onDestroy(){
        super.onDestroy();
        //Free resources
        if(playerManager!=null){
        playerManager.reset();
        playerManager=null;
        }
        if (gameManager != null) {
        gameManager.reset();
        gameManager=null;
        }
        if(connection!=null){
        if(NetworkManager.isNotIdle(connection)){
        connection.terminateConnection();
        }
        connection=null;
        }
        localPlayer=null;
        deck=null;
        discardPile=null;
        gameClient=null;
        }

@Override
protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        localPlayer=null;
        vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        yourTurnTextView=findViewById(R.id.textViewYourTurn);
        seeTheFutureCardTextView=findViewById(R.id.textViewSeeTheFutureCard);
        stealRandomCardTextView=findViewById(R.id.textViewStealRandomCard);
        buttonTwoCats=findViewById(R.id.buttonTwoCats);
        buttonThreeCats=findViewById(R.id.buttonThreeCats);
        connection=NetworkManager.getInstance();
        connection.subscribeCallbackToMessageID(this,GAME_ACTIVITY_DECK_MESSAGE_ID);
        connection.subscribeCallbackToMessageID(this,GAME_ACTIVITY_SHOW_THREE_CARDS_ID);
        connection.subscribeCallbackToMessageID(this,GAME_ACTIVITY_STEAL_CARD);
        connection.subscribeCallbackToMessageID(this,GameManager.GAME_MANAGER_MESSAGE_CHECKED_CARD);
        long seed=System.currentTimeMillis();
        discardPile=new DiscardPile();

        if(NetworkManager.isServer(connection)){
        prepareGame(seed);

        // player id 0 is always the host
        guiInit(playerManager.getLocalSelf());
        playerManager.getLocalSelf().addPropertyChangeListener(yourTurnListener);
        playerManager.getLocalSelf().addPropertyChangeListener(notYourTurnListener);
        playerManager.getLocalSelf().addPropertyChangeListener(cardStolenListener);
        playerManager.getLocalSelf().addPropertyChangeListener(catButtonsInvisibleListener);
        localPlayer=playerManager.getLocalSelf();
        gameManager.startGame();
        }else if(NetworkManager.isClient(connection)){
        deck=null;
        localPlayer=new Player();
        localPlayer.subscribePlayerToCardEvents(connection);
        localPlayer.addPropertyChangeListener(yourTurnListener);
        localPlayer.addPropertyChangeListener(notYourTurnListener);
        localPlayer.addPropertyChangeListener(cardStolenListener);
        localPlayer.addPropertyChangeListener(catButtonsInvisibleListener);
        //playerManager.initializeAsClient(localClientPlayer,connection);
        //gameManager = new GameManager(connection, null,discardPile);
        gameClient=new GameClient(localPlayer,deck,discardPile,connection);

        Toast toast=Toast.makeText(this,"Waiting for host to start",Toast.LENGTH_SHORT);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,100);
        toast.show();

        gameClient.blockUntilReady(this);


        toast=Toast.makeText(this,"You're Player"+localPlayer.getPlayerId(),Toast.LENGTH_SHORT);
        toast.setDuration(Toast.LENGTH_SHORT); // 3 seconds
        toast.setGravity(Gravity.BOTTOM,0,100); // Display at the bottom with an offset
        toast.show();
        guiInit(localPlayer);


        }else if(!NetworkManager.isNotIdle(connection)){
        //this case just for local testing presumably no connection has ever been established
        //Add players to the player's list
        ArrayList<Player> players=new ArrayList<>();
        deck=new Deck(seed);
        Player p1=new Player(1);
        Player p2=new Player(2);
        Player p3=new Player(3);
        Player p4=new Player(4);
        Player p5=new Player(5);
        Player p6=new Player(6);
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

        hintLayout=findViewById(R.id.hint_wrapper);

        findViewById(R.id.close_hint).setOnClickListener(v->hintLayout.setVisibility(View.GONE));
        }

private void prepareGame(long seed){
        deck=new Deck(seed);
        playerManager.initializeAsHost(connection.getServerConnections(),connection);
        ArrayList<Player> players;
        players=new ArrayList<>();
        for(PlayerConnection pc:playerManager.getPlayers()){
        players.add(pc.getPlayer());
        pc.getPlayer().subscribePlayerToCardEvents(connection);
        }
        deck.dealCards(players);
        gameManager=new GameManager(connection,deck,discardPile);
        gameManager.distributeDeck(deck);
        gameManager.distributePlayerHands();
        }

@Override
public void responseReceived(String text,Object sender){
        Log.v("GameActivity",text);
        int messageID=Message.parseAndExtractMessageID(text);
        if(sender instanceof ClientTCP&&(messageID==GAME_ACTIVITY_DECK_MESSAGE_ID)){
        deck=new Deck(Message.parseAndExtractPayload(text));
        if(NetworkManager.isClient(connection)&&gameClient!=null){
        gameClient.setDeck(deck);
        }

        }

        if(messageID==GAME_ACTIVITY_SHOW_THREE_CARDS_ID){
        int playerID=Integer.parseInt(Message.parseAndExtractPayload(text));
        if(playerID!=localPlayer.getPlayerId()){
        displaySeeTheFutureCardText(playerID);
        }
        }

        if(messageID==GAME_ACTIVITY_STEAL_CARD){
        String[]message=Message.parseAndExtractPayload(text).split(":");
        int playerID=Integer.parseInt(message[1]);
        if(playerID==localPlayer.getPlayerId()){
        // steal a random Card and display text
        Card card=localPlayer.removeRandomCardFromHand();
        //send card to stealer
        GameLogic.cardHasBeenGiven(Integer.parseInt(message[0]),connection,card);
        }
        }else if(messageID==GameManager.GAME_MANAGER_MESSAGE_CHECKED_CARD){
        Handler mainHandler=new Handler(Looper.getMainLooper());

        // We can now get the card id from the payload
        // Could be useful for extending the feature
        Runnable myRunnable=()->Toast.makeText(GameActivity.this,"Player looked at a card",Toast.LENGTH_SHORT).show();
        mainHandler.post(myRunnable);
        }
        }

private void displaySeeTheFutureCardText(int playerID){
        runOnUiThread(()->{
        String txt="Player "+playerID+" is watching the top three cards of the deck!";
        seeTheFutureCardTextView.setText(txt);
        seeTheFutureCardTextView.setVisibility(View.VISIBLE);
        });
        new Handler(Looper.getMainLooper()).postDelayed(()->
        seeTheFutureCardTextView.setVisibility(View.INVISIBLE),3000); // 3000 milliseconds = 3 seconds
        }


@Override
public void askForHelp(Card card){
        // Tell others I viewed card
        connection.sendCheckeCard(card);

        String help=getHelp(card);

        TextView hintText=findViewById(R.id.hint);
        hintText.setText(help);

        hintLayout.setVisibility(View.VISIBLE);
        }

private String getHelp(Card card){
        switch(card.getCardID()){
        case ATTACK_CARD_ID:
        return"Do not draw any cards. Instead, immediately force\n"+
        "the next player to take 2 turns in a row. Play then\n"+
        "continues from that player. The victim of this card\n"+
        "takes a turn as normal (play-or-pass then draw).\n"+
        "Then, when their first turn is over, it's their\n"+
        "turn again.\n"+
        "If the victim of an Attack Card plays an Attack\n"+
        "Card on any of their turns, the new target must\n"+
        "take any remaining turns plus the number of\n"+
        "attacks on the Attack Card just played (e.g. 4\n"+
        "turns, then 6, and so on).";
        case BOMB_CARD_ID:
        return"You must show this card immediately. Unless\n"+
        "you have a Defuse Card, you’re dead. Discard\n"+
        "all of your cards, including the Exploding Kitten.";
        case DEFUSE_CARD_ID:
        return"If you drew an Exploding Kitten, you can play\n"+
        "this card instead of dying. Place your Defuse\n"+
        "Card in the Discard Pile.\n"+
        "Want to hurt the player right after you? Put the\n"+
        "Kitten right on top of the deck. If you’d like, hold\n"+
        "the deck under the table so that no one else\n"+
        "can see where you put it.\n"+
        "Your turn is over after playing this card.\n"+
        "Then take the Exploding Kitten, and without\n"+
        "reordering or viewing the other cards, secretly\n"+
        "put it back in the Draw Pile anywhere you’d like.";
        case FAVOR_CARD_ID:
        return"Force any other player to give you 1 card from\n"+
        "their hand. They choose which card to give you.";
        case NOPE_CARD_ID:
        return"\"Stop any action except\n"+
        "for an Exploding Kitten\n"+
        "or a Defuse Card.\n"+
        "Imagine that any card\n"+
        "beneath a Nope Card\n"+
        "never existed.\n"+
        "A Nope can also be\n"+
        "played on another Nope\n"+
        "to negate it and create\n"+
        "a Yup, and so on.\n"+
        "A Nope can be played at any time before an\n"+
        "action has begun, even if it’s not your turn. Any\n"+
        "cards that have been Noped are lost. Leave\n"+
        "them in the Discard Pile.\n"+
        "You can even play a Nope on a\n"+
        "SPECIAL COMBO.\"";
        case SEE_THE_FUTURE_CARD_ID:
        return"Privately view the top 3 cards from the Draw\n"+
        "Pile and put them back in the same order.\n"+
        "Don’t show the cards to the other players.";
        case SHUFFLE_CARD_ID:
        return"Shuffle the Draw Pile thoroughly. (Useful when\n"+
        "you know there’s an Exploding Kitten coming.)";
        case SKIP_CARD_ID:
        return"Immediately end your turn without drawing\n"+
        "a card.\n"+
        "skip 4 cards\n"+
        "If you play a Skip Card as a defense to an Attack\n"+
        "Card, it only ends 1 of the 2 turns. 2 Skip Cards\n"+
        "would end both turns.";
default:
        return"These cards are powerless on their own, but if\n"+
        "you collect any 2 matching Cat Cards, you can\n"+
        "play them as a Pair to steal a random card from\n"+
        "any player.\n"+
        "They can also be used in Special Combos.\n"+
        "\n"+
        "Special Combos:\n"+
        "Two of a kind:\n"+
        "Playing matching Pairs of Cat Cards (where\n"+
        "you get to steal a random card from another\n"+
        "player) no longer only applies to pairs of Cat\n"+
        "Cards. It now applies to ANY pair of cards with\n"+
        "the same title (a pair of Shuffle Cards, a pair of\n"+
        "Skip Cards, etc). Ignore the instructions on the\n"+
        "cards when you play a combo.\n"+
        "\n"+
        "Three of a kind:\n"+
        "When you play 3 matching cards (any 3 cards\n"+
        "with the same title), you get to pick a player and\n"+
        "name a card. If they have that card, they must\n"+
        "give one to you. If they don't have it, you get\n"+
        "nothing. Ignore the instructions on the cards\n"+
        "when you play a combo.\n";
        }
        }
        }
