package com.apps.jlee.boginder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import com.apps.jlee.boginder.Fragments.AccountFragment;
import com.apps.jlee.boginder.Fragments.MatchFragment;
import com.apps.jlee.boginder.Fragments.DateFragment;
import com.apps.jlee.boginder.Fragments.ProgressFragment;
import com.apps.jlee.boginder.R;
import com.apps.jlee.boginder.Service.Constants;
import com.apps.jlee.boginder.Service.FetchIntentService;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AccountFragment accountFragment;
    private MatchFragment matchFragment;
    private DateFragment dateFragment;

    private int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location lastLocation;
    private AddressResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        accountFragment = new AccountFragment();
        matchFragment = new MatchFragment(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        bottomNavigationView.setSelectedItemId(R.id.dates);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.account:
                        setFragment(accountFragment);
                        break;
                    case R.id.dates:
                        hideToolbarItems(true);
                        setFragment(dateFragment);
                        break;
                    case R.id.chat:
                        hideToolbarItems(true);
                        setFragment(matchFragment);
                        break;
                }
                return true;
            }
        });
    }

    private void getLastLocation()
    {
        if (checkPermissions())
        {
            if (isLocationEnabled())
            {
                setFragment(new ProgressFragment());
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Location> task)
                            {
                                lastLocation = task.getResult();
                                if (lastLocation == null)
                                {
                                    requestNewLocationData();
                                }
                                else
                                {
                                    startIntentService();
                                }
                            }
                        }
                );
            }
            else
            {
                enableLocationServices();
            }
        }
        else
        {
            requestPermissions();
        }
    }

    private void enableLocationServices()
    {
        //Define the paramters of our request
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        //Build a location settings request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        //Check if the location settings request is fulfilled
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        //A listener that is called when location settings request are fulfilled
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>()
        {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse)
            {
                // All location settings are satisfied. The client can initialize
                getLastLocation();
            }
        });

        //A listener that is called when location settings request is not fulfilled
        task.addOnFailureListener(this, new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                if (e instanceof ResolvableApiException)
                {
                    // Location settings are not satisfied
                    try
                    {
                        // Show a dialog by calling startResolutionForResult() and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,44);
                    }
                    catch (IntentSender.SendIntentException sendEx)
                    {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    /*If user did not allow location services, it will keep asking it before proceeding to dateFragment*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        getLastLocation();
    }

    private boolean isLocationEnabled()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /*In some devices, if you turn off the location and again turn on, the previous recorded location information will be cleared*/
    private void requestNewLocationData()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,Looper.myLooper());
    }

    /* LocationCallback is an abstract class. This creates an instance of the abstract class where we override onLocationResult().
       When FusedLocationProvider finds the location, the callback method, onLocationResult will be called */
    private LocationCallback mLocationCallback = new LocationCallback()
    {
        @Override
        public void onLocationResult(LocationResult locationResult)
        {
            lastLocation = locationResult.getLastLocation();

            startIntentService();
        }
    };

    private boolean checkPermissions()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        return false;
    }

    /*Opens a dialog box requesting user to grant permission*/
    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ID);
    }

    /*Called after user responds to the dialog box*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getLastLocation();
            }
        }
    }

    protected void startIntentService()
    {
        resultReceiver = new AddressResultReceiver(new Handler());

        Intent intentLocation = new Intent(this, FetchIntentService.class);
        intentLocation.putExtra("intent_type","location");
        intentLocation.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);

        Intent intentProfile = new Intent(this, FetchIntentService.class);
        intentProfile.putExtra("intent_type","profile");
        intentProfile.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        intentProfile.putExtra(Constants.RECEIVER, resultReceiver);

        startService(intentLocation);
        startService(intentProfile);
    }

    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        //Intent intent = new Intent(this,LoginRegisterActivity.class);
        //startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (getCurrentFocus() != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideToolbarItems(boolean hide)
    {
        if (hide)
        {
            for (int i = 0; i < toolbar.getMenu().size(); i++)
            {
                toolbar.getMenu().getItem(i).setVisible(false);
            }
        }
        else
        {
            for (int i = 0; i < toolbar.getMenu().size(); i++)
            {
                toolbar.getMenu().getItem(i).setVisible(true);
            }
        }
    }

    /*This class is very much like an interface I used in CarCare where an interface method was listening in another class and called after user added a gas entry*/
    class AddressResultReceiver extends ResultReceiver
    {
        public AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT)
            {
                ArrayList parcelable = resultData.getParcelableArrayList("Profiles");
                //Log.v("Lakers", parcelable.toString());
                dateFragment = new DateFragment(MainActivity.this,parcelable);
                setFragment(dateFragment);
            }
        }
    }
}
