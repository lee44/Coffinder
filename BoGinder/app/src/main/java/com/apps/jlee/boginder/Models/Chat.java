package com.apps.jlee.boginder.Models;

public class Chat
{
    private String message;
    private Boolean currentUser;

    public Chat(String message, Boolean currentUser)
    {
        this.message = message;
        this.currentUser = currentUser;
    }

    public Boolean getCurrentUser()
    {
        return currentUser;
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
