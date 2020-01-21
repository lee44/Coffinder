package com.apps.jlee.boginder.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable
{
    private String user_id, name, age, height, city, description, profileImageUrl;

    public Card(String user_id, String name, String age, String height, String city, String description, String profileImageUrl)
    {
        this.user_id = user_id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.city = city;
        this.description = description;
        this.profileImageUrl = profileImageUrl;
    }

    public Card(Parcel parcel)
    {
        user_id = parcel.readString();
        name = parcel.readString();
        age = parcel.readString();
        height = parcel.readString();
        city = parcel.readString();
        profileImageUrl = parcel.readString();
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

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public static final Parcelable.Creator<Card> CREATOR =
            new Parcelable.Creator<Card>()
            {

                @Override
                public Card createFromParcel(Parcel source)
                {
                    return new Card(source);
                }

                @Override
                public Card[] newArray(int size)
                {
                    return new Card[size];
                }
            };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(user_id);
        parcel.writeString(name);
        parcel.writeString(age);
        parcel.writeString(height);
        parcel.writeString(city);
        parcel.writeString(profileImageUrl);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
