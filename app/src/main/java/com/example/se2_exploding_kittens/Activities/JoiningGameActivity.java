package com.example.se2_exploding_kittens.Activities;

import static com.example.se2_exploding_kittens.NetworkManager.TEST_MESSAGE_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.se2_exploding_kittens.Network.LobbyLogic.Lobby;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;

public class JoiningGameActivity extends AppCompatActivity implements MessageCallback {
    private NetworkManager client;
    private Lobby lobby;
    int count = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_game);

        client = NetworkManager.getInstance();
        lobby = new Lobby((String) getIntent().getSerializableExtra("name"), (String) getIntent().getSerializableExtra("address"), (Integer) getIntent().getSerializableExtra("port"));
        client.runAsClient(lobby.getAddress(),lobby.getPort());

        client.subscribeCallbackToMessageID(this,TEST_MESSAGE_ID);
        while (count >= 0){

                try {
                    Thread.sleep(250);
                    client.sendMessageFromTheClient(new Message(MessageType.MESSAGE,TEST_MESSAGE_ID,"Ping"));
                    Log.v("MainActivity", "Ping");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            }

        }


    @Override
    public void responseReceived(String text, Object sender) {
        Log.v("MainActivity", text);
        count--;
            //try{
                //client.sendMessageFromTheClient(new Message(MessageType.MESSAGE,TEST_MESSAGE_ID,"Ping"));
            //} catch (IllegalAccessException e) {
                //e.printStackTrace();
                //Log.e("JoiningGameActivity", "is not a client");
            //}
    }

}