package com.example.se2_exploding_kittens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.se2_exploding_kittens.Network.LobbyBroadcaster;
import com.example.se2_exploding_kittens.Network.LobbyListener;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;
import com.example.se2_exploding_kittens.Network.MessageType;
import com.example.se2_exploding_kittens.Network.TCP.ServerTCPSocket;

import java.net.DatagramSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements MessageCallback {

    private Button buttonJoinGame;
    private LobbyBroadcaster lb;
    //private LobbyListener ll;

    //private NetworkManager client ;
    private NetworkManager server ;

    private void addEvtHandler(Button btn, View.OnClickListener listener){
        btn.setOnClickListener(listener);
    }

    public void openJoinGameActivity(){
        //lb.terminateBroadcasting();
        //ll.terminateListening();
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
        //ll = new LobbyListener(this);
        Thread broadcast = new Thread(lb);
        broadcast.start();
        //Thread listener = new Thread(ll);
        //listener.start();
        setContentView(R.layout.activity_main);
        buttonJoinGame = (Button)findViewById(R.id.buttonJoinGame);
        addEvtHandler(buttonJoinGame, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJoinGameActivity();
            }
        });
        //client = new NetworkManager();
        server = new NetworkManager();
        server.runAsServer(45000);
        server.subscribeCallbackToMessageID(this,200);
        //try {
            //server.sendMessageFromTheSever(new Message(MessageType.MESSAGE,200,"200"),server.getServerConnections().get(0));
        //} catch (IllegalAccessException e) {
            //wird geworfen wann als client aufgreufen
            //throw new RuntimeException(e);
        //}

    }

    @Override
    public void responseReceived(String text, Object sender) {
        if(sender instanceof LobbyListener){
            lobbyFound(text);
        }
        if(sender instanceof ServerTCPSocket){
            Log.v("Message", "Echo MSG:"+text);
            try {
                server.sendMessageFromTheSever(new Message(MessageType.REPLY,200,Message.parseAndExtractPayload(text)), (ServerTCPSocket) sender);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}