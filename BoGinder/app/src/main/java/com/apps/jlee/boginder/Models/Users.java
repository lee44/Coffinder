package com.apps.jlee.boginder.Models;

import com.google.firebase.database.PropertyName;
import com.google.firebase.database.connection.RequestResultCallback;
public class Users
{
    @PropertyName("Name")
    private String Name;
    @PropertyName("Gender")
    private String Gender;
    @PropertyName("ProfileImageUrl")
    private String ProfileImageUrl;

    public Users()
    {

    }
    public Users(String Name, String Gender, String ProfileImageUrl)
    {
        this.Name = Name;
        this.Gender = Gender;
        this.ProfileImageUrl = ProfileImageUrl;
    }

    @PropertyName("Name")
    public String getName()
    {
        return Name;
    }
    @PropertyName("Name")
    public void setName(String Name)
    {
        this.Name = Name;
    }

    @PropertyName("Gender")
    public String getGender()
    {
        return Gender;
    }
    @PropertyName("Gender")
    public void setGender(String Gender)
    {
        this.Gender = Gender;
    }

    @PropertyName("ProfileImageUrl")
    public String getProfileImageUrl()
    {
        return ProfileImageUrl;
    }
    @PropertyName("ProfileImageUrl")
    public void setProfileImageUrl(String ProfileImageUrl)
    {
        this.ProfileImageUrl = ProfileImageUrl;
    }

    @Override
    public String toString()
    {
        return "Name: " + Name + " Gender: " + Gender + " ProfileImageUrl: " + ProfileImageUrl;
    }
}
