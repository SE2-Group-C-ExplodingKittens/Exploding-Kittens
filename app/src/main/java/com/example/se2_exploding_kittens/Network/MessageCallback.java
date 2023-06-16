package com.example.se2_exploding_kittens.Network;


public interface MessageCallback {
    void responseReceived(String text, Object sender);
}
