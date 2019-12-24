package com.apps.jlee.boginder.Models;

public class Chat
{
    private String message, receiver_ID, sender_ID;

    public Chat(String message, String receiver_ID, String sender_ID)
    {
        this.message = message;
        this.receiver_ID = receiver_ID;
        this.sender_ID = sender_ID;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }


    public String getReceiver_ID()
    {
        return receiver_ID;
    }

    public void setReceiver_ID(String receiver_ID)
    {
        this.receiver_ID = receiver_ID;
    }

    public String getSender_ID()
    {
        return sender_ID;
    }

    public void setSender_ID(String sender_ID)
    {
        this.sender_ID = sender_ID;
    }
}
