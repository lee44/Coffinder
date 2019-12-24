package com.apps.jlee.boginder.Models;

public class Messages
{
    private String user_id, chat_id, lastMessage;

    public Messages(String user_id,String chat_id, String lastMessage)
    {
        this.user_id = user_id;
        this.chat_id = chat_id;
        this.lastMessage = lastMessage;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getChat_id()
    {
        return chat_id;
    }

    public void setChat_id(String chat_id)
    {
        this.chat_id = chat_id;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }
}
