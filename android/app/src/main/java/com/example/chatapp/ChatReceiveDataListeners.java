package com.example.chatapp;

public interface ChatReceiveDataListeners {
    void onUserJoined(String name);
    void onUserDisconnected(String name);
    void onUserTyping(String user);
    void onUserStopTyping();
    void onMessageReceived(Message message);
}
