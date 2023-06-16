package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.Network.ConnectionState;
import com.example.se2_exploding_kittens.Network.DisconnectedCallback;
import com.example.se2_exploding_kittens.Network.Message;
import com.example.se2_exploding_kittens.Network.MessageCallback;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.nio.channels.SocketChannel;

public class ServerTCPSocket implements Runnable, TCP{

    //The server just listens to the socket for a connection request (Socket clientSocketConnection = serverSocket.accept();)
    //On the accept method, it blocks until a connection request from a client.
    //this Class is supposed to be created from the clientSocketConnection

    private Socket connection = null;
    private BufferedReader in = null;
    private DataOutputStream out = null;
    private MessageCallback defaultCallback = null;
    private DisconnectedCallback disconnectedCallback = null;
    private ConnectionState connState = ConnectionState.IDLE;
    private ArrayList<Message> messages = new ArrayList<Message>();

    public ServerTCPSocket(Socket connection){
        this.connection = connection;
        try {
            out = new DataOutputStream (connection.getOutputStream());
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
            connState = ConnectionState.CONNECTED;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addMessage(Message message) {
        if(connState == ConnectionState.CONNECTED){
            messages.add(message);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void setDefaultCallback(MessageCallback defaultCallback) {
        this.defaultCallback = defaultCallback;
    }

    public void setDisconnectCallback(DisconnectedCallback disconnectedCallback){
        this.disconnectedCallback = disconnectedCallback;
    }

    @Override
    public void endConnection() {
        connState = ConnectionState.DISCONNECTING;
        try {
            out.close();
            in.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages(BufferedReader in) throws IOException {
        String response = null;
        response = in.readLine();
        if(response == null){
            //EOF sent
            connState = ConnectionState.DISCONNECTING;
        }else {
            if (defaultCallback != null) {
                defaultCallback.responseReceived(response, this);
            }
        }
    }

    private boolean checkConnectionDisconnected(Socket connection){
        //tcp sends EOF on disconnect
        if (connection.isClosed() || connState != ConnectionState.CONNECTED) {
            connState = ConnectionState.DISCONNECTING;
            return true;
        }
        return false;
    }

    private void startSenderThread(){
        //separate thread to write
        new Thread(() -> {
            try {
                while (connState == ConnectionState.CONNECTED){
                    //drop empty messages
                    if(messages.size() == 0){
                        continue;
                    }
                    if(messages.get(0) == null){
                        messages.remove(0);
                        continue;
                    }else{
                        out.writeBytes(messages.get(0).getTransmitMessage() + "\n");
                        messages.remove(0);
                    }
                    Thread.sleep(20);
                }
            } catch (IOException e) {
                connState = ConnectionState.DISCONNECTING;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void run() {
        if(connState == ConnectionState.CONNECTED){
            try {

                startSenderThread();

                while (connState == ConnectionState.CONNECTED){
                    //wait for messages
                    if(checkConnectionDisconnected(connection)){
                        break;
                    }
                    listenForMessages(in);
                }
                if(connState == ConnectionState.DISCONNECTING){
                    connState = ConnectionState.DISCONNECTED;
                    out.close();
                    in.close();
                    connection.close();
                    if(disconnectedCallback != null)
                        disconnectedCallback.connectionDisconnected(this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
