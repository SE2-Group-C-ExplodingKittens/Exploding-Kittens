package com.example.se2_exploding_kittens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.se2_exploding_kittens.Network.LobbyListener;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.TCP.Lobby;

import java.util.ArrayList;

public class JoinGameActivity extends AppCompatActivity implements MessageCallback {

    private LobbyListener ll;
    private NetworkManager client ;

    private ArrayList<Lobby> lobbies;

    private void addEvtHandler(Button btn, View.OnClickListener listener){
        btn.setOnClickListener(listener);
    }

    public void openJoinGameActivity(){
        ll.terminateListening();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        ll = new LobbyListener(this);
        Thread listener = new Thread(ll);
        listener.start();

        client = new NetworkManager();
        while (true){
            if(ll.getLobbies().size() > 0){
                //client.runAsClient(ll.getLobbies().get(0).getAddress(),ll.getLobbies().get(0).getPort());
                //client.subscribeCallbackToMessageID(this,200);
                try {
                    Thread.sleep(250);
                    //client.sendMessageFromTheClient(new Message(MessageType.MESSAGE,200,"200"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void responseReceived(String text, Object sender) {
        if(sender instanceof LobbyListener){
            lobbyFound(text);
        }
    }

    private void lobbyFound(String text) {
        lobbies = ll.getLobbies();
        //view update
    }

}