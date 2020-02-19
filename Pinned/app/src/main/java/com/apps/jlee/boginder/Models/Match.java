package com.apps.jlee.boginder.Models;

import java.util.ArrayList;

public class Match
{
    private ArrayList<String> profileImageUrl;
    private String user_id, name, chat_id, message, message_direction;

    public Match(String user_id, String name, ArrayList<String> profileImageUrl, String chat_id, String message, String message_direction)
    {
        this.user_id = user_id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.chat_id = chat_id;
        this.message = message;
        this.message_direction = message_direction;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public ArrayList<String> getProfileImageUrl()
    {
        return profileImageUrl;
    }

    public void setProfileImageUrl(ArrayList<String> profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getChat_id()
    {
        return chat_id;
    }

    public void setChat_id(String chat_id)
    {
        this.chat_id = chat_id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage_direction()
    {
        return message_direction;
    }

    public void setMessage_direction(String message_direction)
    {
        this.message_direction = message_direction;
    }

    @Override
    public String toString()
    {
        return "User_ID: " + user_id +", Name: "+ name +", Chat_ID: "+ chat_id +", Message: "+ message +", Message_Direction: " + message_direction;
    }
}
