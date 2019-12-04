package com.apps.jlee.boginder.Models;

import com.google.firebase.database.PropertyName;

public class Users
{
    @PropertyName("Name")
    private String name;
    @PropertyName("Gender")
    private String gender;
    @PropertyName("ProfileImageUrl")
    private String profileImageUrl;
    @PropertyName("DeviceToken")
    private String device_token;

    public Users()
    {

    }
    public Users(String name, String gender, String profileImageUrl, String device_token)
    {
        this.name = name;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.device_token = device_token;
    }

    @PropertyName("Name")
    public String getName()
    {
        return name;
    }

    @PropertyName("Name")
    public void setName(String name)
    {
        this.name = name;
    }

    @PropertyName("Gender")
    public String getGender()
    {
        return gender;
    }

    @PropertyName("Gender")
    public void setGender(String gender)
    {
        this.gender = gender;
    }

    @PropertyName("ProfileImageUrl")
    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }

    @PropertyName("ProfileImageUrl")
    public void setProfileImageUrl(String profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }

    @PropertyName("DeviceToken")
    public String getDevice_token()
    {
        return device_token;
    }

    @PropertyName("DeviceToken")
    public void setDevice_token(String device_token)
    {
        this.device_token = device_token;
    }

    @Override
    public String toString()
    {
        return "Name: " + name + " gender: " + gender + " profileImageUrl: " + profileImageUrl;
    }
}
