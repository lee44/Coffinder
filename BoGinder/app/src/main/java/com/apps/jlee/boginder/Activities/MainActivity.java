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
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.apps.jlee.boginder.Fragments.AccountFragment;
import com.apps.jlee.boginder.Fragments.MatchFragment;
import com.apps.jlee.boginder.Fragments.DateFragment;
import com.apps.jlee.boginder.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        accountFragment = new AccountFragment();
        matchFragment = new MatchFragment(this);
        dateFragment = new DateFragment(this);

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

    @SuppressLint("MissingPermission")
    private void getLastLocation()
    {
        if (checkPermissions())
        {
            if (isLocationEnabled())
            {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Location> task)
                            {
                                Location location = task.getResult();
                                if (location == null)
                                {
                                    requestNewLocationData();
                                }
                                else
                                {
                                    Bundle bundle = new Bundle();
                                    bundle.putDouble("Latitude",location.getLatitude());
                                    bundle.putDouble("Longitude",location.getLongitude());
                                    dateFragment.setArguments(bundle);
                                    setFragment(dateFragment);
                                }
                            }
                        }
                );
            }
            else
            {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else
        {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
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

    private LocationCallback mLocationCallback = new LocationCallback()
    {
        @Override
        public void onLocationResult(LocationResult locationResult)
        {
            Location mLastLocation = locationResult.getLastLocation();

            Bundle bundle = new Bundle();
            bundle.putDouble("Latitude",mLastLocation.getLatitude());
            bundle.putDouble("Longitude",mLastLocation.getLongitude());
            dateFragment.setArguments(bundle);
            setFragment(dateFragment);
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

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ID);
    }

    private boolean isLocationEnabled()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

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
}
