package com.example.se2_exploding_kittens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.se2_exploding_kittens.Network.LobbyLogic.LobbyBroadcaster;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;

public class HostGameActivity extends AppCompatActivity {

    private LobbyBroadcaster lb;
    private NetworkManager connection;
    private Button buttonStartHosting;
    private Button buttonStartGame;
    private EditText editTextLobbyName;

    protected void onDestroy() {
        super.onDestroy();
        lb.terminateBroadcasting();
        if(connection.getConnectionRole() != TypeOfConnectionRole.IDLE)
            connection.terminateConnection();
    }

    private void hostLobby(String name){
        lb = new LobbyBroadcaster(name, 45000);
        Thread broadcast = new Thread(lb);
        broadcast.start();
        connection = NetworkManager.getInstance();
        connection.runAsServer(45000);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        buttonStartHosting = findViewById(R.id.buttonStartHosting);
        buttonStartGame = findViewById(R.id.buttonStartGame);
        buttonStartGame.setEnabled(false);
        editTextLobbyName = findViewById(R.id.editTextLobbyName);

        buttonStartHosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lobbyName = editTextLobbyName.getText().toString().trim();
                if (!lobbyName.isEmpty()) {
                    hostLobby(lobbyName);
                }else{
                    hostLobby("Lobby");
                }
                buttonStartHosting.setEnabled(false);
                buttonStartGame.setEnabled(true);
            }
        });

        buttonStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostGameActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }
}