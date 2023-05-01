package com.example.se2_exploding_kittens.Network.LobbyLogic;


import com.example.se2_exploding_kittens.Network.IPUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LobbyBroadcaster implements Runnable{

    private String lobbyName;
    private int port;
    private boolean terminateBroadcasting = false;

    public LobbyBroadcaster(String lobbyName, int port){
        lobbyName=lobbyName.replaceAll("#","");
        this.lobbyName = lobbyName;
        this.port = port;
    }

    public void terminateBroadcasting(){
        terminateBroadcasting = true;
    }

    public void broadcastLobbyInfo(String lobbyName) {
        try {
            // Create a new DatagramSocket for sending UDP packets
            DatagramSocket socket = new DatagramSocket();

            // Define the broadcast address and port number for the UDP packets
            InetAddress broadcastAddress = IPUtil.getLocalBroadcastAddress();
            int port = 51600;

            // Create a byte array to hold the lobby information data
            byte[] lobbyData = ((lobbyName+"#"+this.port)).getBytes();

            // Create a new DatagramPacket with the lobby information data, broadcast address, and port number
            DatagramPacket packet = new DatagramPacket(lobbyData, lobbyData.length, broadcastAddress, port);

            // Send the UDP packet
            do{
                socket.send(packet);
                Thread.sleep(200);
            }while (!terminateBroadcasting);


            // Close the socket when we're done sending packets
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        broadcastLobbyInfo(lobbyName);
    }
}
