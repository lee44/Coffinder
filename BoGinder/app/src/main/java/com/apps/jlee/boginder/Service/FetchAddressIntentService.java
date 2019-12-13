package com.apps.jlee.boginder.Service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
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

public class FetchAddressIntentService extends IntentService
{
    protected ResultReceiver receiver;
    protected DatabaseReference databaseReference;
    private String currentUser_id;

    public FetchAddressIntentService()
    {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (intent == null)
        {
            return;
        }
        String errorMessage = "";

        // Get the location passed to this service through an extra since a Location Object was passed instead of a normal string or int
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        receiver = intent.getParcelableExtra(Constants.RECEIVER);

        List<Address> addresses = null;

        try
        {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch (IOException ioException)
        {
            // Catch network or other I/O problems.
            errorMessage = "Service not available";
            Log.v("Lakers", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException)
        {
            // Catch invalid latitude or longitude values.
            errorMessage = "Invalid latitude and longitude";
            Log.v("Lakers", errorMessage + ". " + "Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0)
        {
            if (errorMessage.isEmpty())
            {
                errorMessage = "No Address found";
                Log.v("Lakers", errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
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
            deliverResultToReceiver(Constants.SUCCESS_RESULT,TextUtils.join(System.getProperty("line.separator"),addressFragments));

            currentUser_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser_id);

            String [] split = addresses.get(0).getAddressLine(0).split(",");
            String streetName = split[0];
            String cityName = split[1];
            String stateName = split[2];
            String countryName = split[3];

           databaseReference.child("City").setValue(cityName);
           Location lastLocation = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
           databaseReference.child("Latitude").setValue(lastLocation.getLatitude());
           databaseReference.child("Longitude").setValue(lastLocation.getLongitude());
        }
    }

    private void deliverResultToReceiver(int resultCode, String message)
    {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        receiver.send(resultCode, bundle);
    }
}
