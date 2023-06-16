package com.example.se2_exploding_kittens.Network.LobbyLogic;

import com.example.se2_exploding_kittens.Network.MessageCallback;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class LobbyListener implements Runnable {

    private boolean terminateListening = false;
    private MessageCallback packetReceivedCallback;
    private ArrayList <Lobby> lobbies = new ArrayList<Lobby>();

    public LobbyListener(MessageCallback packetReceivedCallback){
        this.packetReceivedCallback = packetReceivedCallback;
    }

    public void terminateListening(){
        terminateListening = true;
    }


    public void startListening() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(51600);

            int timeoutMillis = 1000;  // wait for 1 sec
            socket.setSoTimeout(timeoutMillis);

            byte[] buffer = new byte[1024];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (!terminateListening) {
                // wait for UDP packet
                if (listenForPackets(socket, packet)) continue;

                parseLobbyInformationFromPacket(packet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null){
                socket.close();
            }
        }
    }

    private void parseLobbyInformationFromPacket(DatagramPacket packet) {
        String packetData = new String(packet.getData(), 0, packet.getLength());
        String packetSrcAddr = packet.getAddress().toString();
        packetSrcAddr = packetSrcAddr.replaceAll("/","");
        String[] split = packetData.split("#");
        if(split.length == 2){
            int lobbyPort = Integer.parseInt(split[1]);
            String name = split[0];
            Lobby lobby = new Lobby(name, packetSrcAddr, lobbyPort);
            boolean lobbyExists = false;
            for (Lobby l:lobbies) {
                if(l.getName().equals(lobby.getName()) && l.getAddress().equals(lobby.getAddress())){
                    lobbyExists = true;
                    break;
                }
            }
            if(!lobbyExists){
                lobbies.add(lobby);
                packetReceivedCallback.responseReceived(lobbies.indexOf(lobby)+"", this);
            }
        }
    }

    private boolean listenForPackets(DatagramSocket socket, DatagramPacket packet) throws IOException {
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e) {
            // timeout, no data received
            return true;
        }
        return false;
    }

    public ArrayList<Lobby> getLobbies(){
        return lobbies;
    }

    @Override
    public void run() {
        startListening();
    }
}
