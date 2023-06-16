package com.example.se2_exploding_kittens.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.se2_exploding_kittens.Lobby_RecyclerViewAdapter;
import com.example.se2_exploding_kittens.Network.LobbyLogic.LobbyListener;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.LobbyLogic.Lobby;
import com.example.se2_exploding_kittens.Network.LobbyLogic.JoinLobbyCallback;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;


import java.util.ArrayList;

public class JoinGameActivity extends AppCompatActivity implements MessageCallback, JoinLobbyCallback {

    private LobbyListener ll;

    private NetworkManager client ;

    private RecyclerView lobbyView;

    private ArrayList<Lobby> lobbies;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ll != null)
            ll.terminateListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        lobbyView = findViewById(R.id.lobbyView);
        ll = new LobbyListener(this);
        Thread listener = new Thread(ll);
        listener.start();

        lobbies = ll.getLobbies();

        LobbyRecyclerViewAdapter lobby_recyclerViewAdapter = new LobbyRecyclerViewAdapter(this, lobbies, this::JoinLobby);
        lobbyView.setAdapter(lobby_recyclerViewAdapter);
        lobbyView.setLayoutManager(new LinearLayoutManager(this));
        client = NetworkManager.getInstance();
    }
    @Override
    public void responseReceived(String text, Object sender) {
        if(sender instanceof LobbyListener){
            lobbyFound();
        }
    }

    private void lobbyFound(){
        lobbies = ll.getLobbies();
        runOnUiThread(() -> lobbyView.getAdapter().notifyDataSetChanged());
    }

    @Override
    public void JoinLobby(Lobby lobby) {
        if(lobby == null)
            throw new NullPointerException();
        ll.terminateListening();
        //https://www.tutorialspoint.com/how-to-pass-an-object-from-one-activity-to-another-in-android

        client.runAsClient(lobby.getAddress(),lobby.getPort());
        runOnUiThread(() -> {
            Intent intent = new Intent(JoinGameActivity.this, GameActivity.class);
            startActivity(intent);
        });
    }
}