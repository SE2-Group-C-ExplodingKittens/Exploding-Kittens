package com.example.se2_exploding_kittens.Network;


import java.net.Socket;

public interface MessageCallback {
    void responseReceived(String text, Object sender);
}
