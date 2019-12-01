package com.apps.jlee.boginder.Models;

public class Cards
{
    private String user_id, name;
    private String profileImageUrl;

    public Cards(String user_id,String name,String profileImageUrl)
    {
        this.user_id = user_id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }
}
