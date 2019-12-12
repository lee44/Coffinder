package com.apps.jlee.boginder.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apps.jlee.boginder.R;
import com.edmodo.rangebar.RangeBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreferencesFragment extends Fragment
{
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.male_radio_button)
    RadioButton male_radio_button;
    @BindView(R.id.female_radio_button)
    RadioButton female_radio_button;
    @BindView(R.id.age_rangeBar)
    RangeBar ageBar;
    @BindView(R.id.height_rangeBar)
    RangeBar heightBar;
    @BindView(R.id.distance_rangeBar)
    RangeBar distanceBar;
    @BindView(R.id.age_range_tv)
    TextView age_range_tv;
    @BindView(R.id.height_range_tv)
    TextView height_range_tv;
    @BindView(R.id.distance_range_tv)
    TextView distance_range_tv;

    private DatabaseReference databaseReference;
    final int startingHeight = 48;
    private int startingInches, endingInches, startingFeet, endingFeet, age_low, age_high, height_low, height_high, distance_low, distance_high;
    private String user_id;

    public PreferencesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Enables the toolbar menu
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users/"+user_id);

        ButterKnife.bind(this,view);

        ageBar.setTickCount(33);
        ageBar.setTickHeight(0);
        ageBar.setBarWeight(6);

        heightBar.setTickCount(37);
        heightBar.setTickHeight(0);
        heightBar.setBarWeight(6);

        distanceBar.setTickCount(50);
        distanceBar.setTickHeight(0);
        distanceBar.setBarWeight(6);

        getUserOrientation();
        getUserSharedPreferences();

        ageBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener()
        {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1)
            {
                age_low = i+18;
                age_high = i1+18;
                age_range_tv.setText(age_low+"-"+age_high);
            }
        });

        heightBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener()
        {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1)
            {
                startingInches = startingHeight + i;
                endingInches = startingHeight + i1;

                height_low = startingInches;
                height_high = endingInches;

                startingFeet = startingInches/12;
                endingFeet = endingInches/12;

                startingInches = startingInches % 12;
                endingInches = endingInches % 12;

                String height = startingFeet+"'"+startingInches+'"'+"-"+endingFeet+"'"+endingInches+'"';

                height_range_tv.setText(height);
            }
        });

        distanceBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener()
        {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1)
            {
                distance_low = i;
                distance_high = i1;

                distance_range_tv.setText(distance_low+"-"+distance_high+" mi");
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        saveUserPreferences();
        return true;
    }

    private void getUserOrientation()
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0)
                {
                    Map<String, Object> map = (Map<String, Object>)dataSnapshot.getValue();

                    if(map.get("Orientation") != null)
                        ((RadioButton)radioGroup.findViewById(map.get("Orientation").toString().equals("Men") ? R.id.male_radio_button : R.id.female_radio_button)).setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    private void saveUserPreferences()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("Age_Low",age_low);
        editor.putInt("Age_High",age_high);

        editor.putInt("Height_Low",height_low);
        editor.putInt("Height_High",height_high);

        editor.putInt("Distance_Low",distance_low);
        editor.putInt("Distance_High",distance_high);

        editor.apply();
    }

    private void getUserSharedPreferences()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        age_low = sharedPreferences.getInt("Age_Low",18);
        age_high = sharedPreferences.getInt("Age_High",50);
        age_range_tv.setText(age_low+"-"+age_high);
        ageBar.setThumbIndices(age_low-18,age_high-18);

        height_low = sharedPreferences.getInt("Height_Low",48);
        height_high = sharedPreferences.getInt("Height_High",84);

        heightBar.setThumbIndices(height_low-48,height_high-48);

        String height = getHeight(height_low)+"-"+getHeight(height_high);
        height_range_tv.setText(height);

        distance_low = sharedPreferences.getInt("Distance_Low",0);
        distance_high = sharedPreferences.getInt("Distance_High",50);
        distance_range_tv.setText(distance_low+"-"+distance_high);
        distanceBar.setThumbIndices(distance_low,distance_high);
    }

    public String getHeight(int inches)
    {
        int feet = inches/12;
        inches = inches%12;

        return feet+"'"+inches+'"';
    }
}
