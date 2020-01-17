package com.apps.jlee.boginder.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.apps.jlee.boginder.Models.Cards;
import com.google.android.material.internal.ParcelableSparseArray;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FetchIntentService extends IntentService
{
    protected ResultReceiver receiver;
    protected DatabaseReference databaseReference;
    protected ChildEventListener oppositeGenderDB;
    protected ValueEventListener valueEventListener;
    protected Location location;
    protected ArrayList<Cards> cardsList;
    protected String currentUser_id, orientation;
    protected int count = 0;

    public FetchIntentService()
    {
        super("FetchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent == null)
        {
            return;
        }
        else if(intent.getExtras().getString("intent_type").equals("location"))
        {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            String errorMessage = "";

            // Get the location passed to this service through an extra since a Location Object was passed instead of a normal string or int
            location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

            List<Address> addresses = null;

            try
            {
                if (location != null)
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException ioException)
            {
                // Catch network or other I/O problems.
                errorMessage = "Service not available";
            } catch (IllegalArgumentException illegalArgumentException)
            {
                // Catch invalid latitude or longitude values.
                errorMessage = "Invalid latitude and longitude";
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0)
            {
                if (errorMessage.isEmpty())
                {
                    errorMessage = "No Address found";
                }
            }
            else
            {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++)
                {
                    addressFragments.add(address.getAddressLine(i));
                }
                //deliverResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"), addressFragments));

                currentUser_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser_id);

                String[] split = addresses.get(0).getAddressLine(0).split(",");
                String streetName = split[0].trim();
                String cityName = split[1].trim();
                String stateName = split[2].trim();
                String countryName = split[3].trim();

                databaseReference.child("City").setValue(cityName);
                Location lastLocation = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
                databaseReference.child("Latitude").setValue(lastLocation.getLatitude());
                databaseReference.child("Longitude").setValue(lastLocation.getLongitude());
            }
        }
        else
        {
            cardsList = new ArrayList<>();
            currentUser_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
            receiver = intent.getParcelableExtra(Constants.RECEIVER);
            location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

            checkUserOrientation();
        }
    }

    public void checkUserOrientation()
    {
        DatabaseReference userDB = databaseReference.child(currentUser_id);

        userDB.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            /*Callback method onChildAdded is triggered once for each existing child and for every new child added*/
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getKey().equals(currentUser_id))
                {
                    orientation = dataSnapshot.child("Orientation").getValue().toString();
                    getPotentialMatches();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    public void getPotentialMatches()
    {
        oppositeGenderDB = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                    if(!dataSnapshot.child("Connections/Nope/").hasChild(currentUser_id) && !dataSnapshot.child("Connections/Yes/").hasChild(currentUser_id))
                    {
                        SharedPreferences sp = getApplicationContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        int ageLow = sp.getInt("Age_Low",18);
                        int ageHigh = sp.getInt("Age_High",50);
                        int userAge = Integer.valueOf(dataSnapshot.child("Age").getValue().toString());

                        String heightLow = getHeight(sp.getInt("Height_Low",48));
                        String heightHigh = getHeight(sp.getInt("Height_High",84));
                        String userHeight = dataSnapshot.child("Height").getValue().toString();

                        int distanceLow = sp.getInt("Distance_Low",0);
                        int distanceHigh = sp.getInt("Distance_High",50);
                        double distanceTo = calculateDistance(location.getLatitude(),location.getLongitude(),Double.valueOf(dataSnapshot.child("Latitude").getValue().toString()),Double.valueOf(dataSnapshot.child("Longitude").getValue().toString()),"M");

                        if (ageLow <= userAge && ageHigh >= userAge && heightLow.compareTo(userHeight) <= 0 && heightHigh.compareTo(userHeight) >= 0 && distanceLow <= distanceTo && distanceHigh >= distanceTo)
                        {
                            if (orientation.equals("Men") && dataSnapshot.child("Gender").getValue().toString().equals("Male"))
                            {
                                cardsList.add(new Cards(dataSnapshot.getKey(), dataSnapshot.child("Name").getValue().toString(), dataSnapshot.child("Age").getValue().toString(),
                                        dataSnapshot.child("Height").getValue().toString(), dataSnapshot.child("City").getValue().toString(),
                                        dataSnapshot.child("ProfileImageUrl").getValue().toString()));
                            }
                            else if (orientation.equals("Women") && dataSnapshot.child("Gender").getValue().toString().equals("Female"))
                            {
                                cardsList.add(new Cards(dataSnapshot.getKey(), dataSnapshot.child("Name").getValue().toString(), dataSnapshot.child("Age").getValue().toString(),
                                        dataSnapshot.child("Height").getValue().toString(), dataSnapshot.child("City").getValue().toString(),
                                        dataSnapshot.child("ProfileImageUrl").getValue().toString()));
                            }
                        }
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot){}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        };

        valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                deliverResultToReceiver(Constants.SUCCESS_RESULT, "Finished");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        };

        databaseReference.addChildEventListener(oppositeGenderDB);
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    public String getHeight(int inches)
    {
        int feet = inches/12;
        inches = inches%12;

        return feet+"'"+inches+'"';
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2, String unit)
    {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit.equals("K"))
        {
            dist = dist * 1.609344;
        }
        else if (unit.equals("M"))
        {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad)
    {
        return (rad * 180.0 / Math.PI);
    }

    private void deliverResultToReceiver(int resultCode, String message)
    {
        databaseReference.removeEventListener(oppositeGenderDB);
        databaseReference.removeEventListener(valueEventListener);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putParcelableArrayList("Profiles",cardsList);
        receiver.send(resultCode, bundle);
    }
}
