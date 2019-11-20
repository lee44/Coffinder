package com.apps.jlee.boginder;

public class Cards
{
    private String user_id, name;

    public Cards(String user_id,String name)
    {
        this.user_id = user_id;
        this.name = name;
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
}
