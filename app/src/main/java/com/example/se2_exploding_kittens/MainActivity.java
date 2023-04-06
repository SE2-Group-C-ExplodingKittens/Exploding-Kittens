package com.example.se2_exploding_kittens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.se2_exploding_kittens.Network.LobbyBroadcaster;
import com.example.se2_exploding_kittens.Network.LobbyListener;

public class MainActivity extends AppCompatActivity implements MessageCallback{

    private Button buttonJoinGame;
    private LobbyBroadcaster lb;
    private LobbyListener ll;

    private void addEvtHandler(Button btn, View.OnClickListener listener){
        btn.setOnClickListener(listener);
    }

    public void openJoinGameActivity(){
        lb.terminateBroadcasting();
        ll.terminateListening();
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);
    }

    private void lobbyFound(String s){
        Log.v("lobbyFound", "idx:"+s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lb = new LobbyBroadcaster("L1", 45000);
        ll = new LobbyListener(this);
        Thread broadcast = new Thread(lb);
        broadcast.start();
        Thread listener = new Thread(ll);
        listener.start();
        setContentView(R.layout.activity_main);
        buttonJoinGame = (Button)findViewById(R.id.buttonJoinGame);
        addEvtHandler(buttonJoinGame, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJoinGameActivity();
            }
        });
    }

    @Override
    public void responseReceived(String text) {
        lobbyFound(text);
    }
}