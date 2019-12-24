package com.apps.jlee.boginder.Models;

public class Matches
{
    private String user_id, name, profileImageUrl, chat_id, message;

    public Matches(String user_id,String name, String profileImageUrl, String chat_id, String message)
    {
        this.user_id = user_id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.chat_id = chat_id;
        this.message = message;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl)
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
}
