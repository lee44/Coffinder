package com.apps.jlee.boginder.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apps.jlee.boginder.R;
import com.edmodo.rangebar.RangeBar;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreferencesFragment extends Fragment
{
    @BindView(R.id.age_rangeBar)
    RangeBar ageBar;
    @BindView(R.id.height_rangeBar)
    RangeBar heightBar;
    @BindView(R.id.distance_seekBar)
    SeekBar distanceBar;
    @BindView(R.id.age_range_tv)
    TextView age_range_tv;
    @BindView(R.id.height_range_tv)
    TextView height_range_tv;
    @BindView(R.id.distance_range_tv)
    TextView distance_range_tv;

    final int startingHeight = 48;
    private int startingInches, endingInches, startingFeet, endingFeet;

    public PreferencesFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        ButterKnife.bind(this,view);

        ageBar.setTickCount(33);
        ageBar.setTickHeight(0);
        ageBar.setBarWeight(6);

        heightBar.setTickCount(37);
        heightBar.setTickHeight(0);
        heightBar.setBarWeight(6);

        ageBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener()
        {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1)
            {
                age_range_tv.setText((i+18)+"-"+(i1+18));
            }
        });

        heightBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener()
        {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1)
            {
                startingInches = startingHeight + i;
                endingInches = startingHeight + i1;

                startingFeet = startingInches/12;
                endingFeet = endingInches/12;

                startingInches = startingInches % 12;
                endingInches = endingInches % 12;

                String height = startingFeet+"'"+startingInches+'"'+"-"+endingFeet+"'"+endingInches+'"';
                height_range_tv.setText(height);
            }
        });

        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                distance_range_tv.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        return view;
    }

}
