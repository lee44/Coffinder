package com.apps.jlee.boginder.Models;

public class Cards
{
    private String user_id, name, age, height, distance;
    private String profileImageUrl;

    public Cards(String user_id, String name, String age, String height, String distance, String profileImageUrl)
    {
        this.user_id = user_id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.distance = distance;
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

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }
}
