package com.apps.jlee.coffinder.Models;

import com.google.firebase.database.PropertyName;

public class Users
{
    @PropertyName("Name")
    private String name;
    @PropertyName("Age")
    private String age;
    @PropertyName("Height")
    private String height;
    @PropertyName("Gender")
    private String gender;
    @PropertyName("Orientation")
    private String orientation;
    @PropertyName("City")
    private String city;
    @PropertyName("ProfileImageUrl")
    private String profileImageUrl;
    @PropertyName("DeviceToken")
    private String device_token;

    public Users(){}
    public Users(String name, String age, String height, String gender, String orientation, String city, String profileImageUrl, String device_token)
    {
        this.name = name;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.orientation = orientation;
        this.city = city;
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

    @PropertyName("Orientation")
    public String getOrientation()
    {
        return orientation;
    }

    @PropertyName("Orientation")
    public void setOrientation(String orientation)
    {
        this.orientation = orientation;
    }

    @PropertyName("City")
    public String getCity()
    {
        return city;
    }

    @PropertyName("City")
    public void setCity(String city)
    {
        this.city = city;
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

    @PropertyName("Age")
    public String getAge()
    {
        return age;
    }
    @PropertyName("Age")
    public void setAge(String age)
    {
        this.age = age;
    }
    @PropertyName("Height")
    public String getHeight()
    {
        return height;
    }
    @PropertyName("Height")
    public void setHeight(String height)
    {
        this.height = height;
    }

    @Override
    public String toString()
    {
        return "Name: " + name + " gender: " + gender + " profileImageUrl: " + profileImageUrl;
    }
}
