package com.apps.jlee.boginder.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Cards implements Parcelable
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

    public Cards(Parcel parcel)
    {
        user_id = parcel.readString();
        name = parcel.readString();
        age = parcel.readString();
        height = parcel.readString();
        distance = parcel.readString();
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

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public static final Parcelable.Creator<Cards> CREATOR =
            new Parcelable.Creator<Cards>()
            {

                @Override
                public Cards createFromParcel(Parcel source)
                {
                    return new Cards(source);
                }

                @Override
                public Cards[] newArray(int size)
                {
                    return new Cards[size];
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
        parcel.writeString(distance);
        parcel.writeString(profileImageUrl);
    }
}
