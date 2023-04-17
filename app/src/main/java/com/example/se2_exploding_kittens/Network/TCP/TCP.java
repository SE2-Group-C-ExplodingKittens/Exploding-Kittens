package com.example.se2_exploding_kittens.Network.TCP;

import com.example.se2_exploding_kittens.Message;
import com.example.se2_exploding_kittens.MessageCallback;

public interface TCP {

    public boolean addMessage(Message message);

    public void setDefaultCallback(MessageCallback defaultCallback);

    public void endConnection();

}
