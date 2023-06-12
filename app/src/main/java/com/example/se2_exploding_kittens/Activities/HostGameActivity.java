package com.example.se2_exploding_kittens.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.se2_exploding_kittens.Network.IPUtil;
import com.example.se2_exploding_kittens.Network.LobbyLogic.LobbyBroadcaster;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;

public class HostGameActivity extends AppCompatActivity {

    private LobbyBroadcaster lb;
    private NetworkManager connection;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(lb != null)
            lb.terminateBroadcasting();
        if(connection != null){
            if(connection.getConnectionRole() != TypeOfConnectionRole.IDLE)
                connection.terminateConnection();
        }
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
        Button buttonStartHosting;
        Button buttonStartGame;
        EditText editTextLobbyName;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        buttonStartHosting = findViewById(R.id.buttonStartHosting);
        buttonStartGame = findViewById(R.id.buttonStartGame);
        buttonStartGame.setEnabled(false);
        editTextLobbyName = findViewById(R.id.editTextLobbyName);

        buttonStartHosting.setOnClickListener(v -> {
            String lobbyName = editTextLobbyName.getText().toString().trim();
            if(IPUtil.getLocalBroadcastAddress() == null){
                Toast toast = Toast.makeText(this, "Connect to the local wifi!", Toast.LENGTH_LONG);
                toast.setDuration(Toast.LENGTH_SHORT); // 3 seconds
                toast.setGravity(Gravity.BOTTOM, 0, 100); // Display at the bottom with an offset
                toast.show();
            }else{
                if (!lobbyName.isEmpty()) {
                    hostLobby(lobbyName);
                }else{
                    hostLobby("Lobby");
                }
                buttonStartHosting.setEnabled(false);
                buttonStartGame.setEnabled(true);
            }

        });

        buttonStartGame.setOnClickListener(v -> {
            lb.terminateBroadcasting();
            Intent intent = new Intent(HostGameActivity.this, GameActivity.class);
            startActivity(intent);
        });
    }
}