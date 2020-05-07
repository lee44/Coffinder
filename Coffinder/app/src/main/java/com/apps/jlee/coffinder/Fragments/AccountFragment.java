package com.apps.jlee.coffinder.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.apps.jlee.coffinder.R;
import com.google.android.material.tabs.TabLayout;


public class AccountFragment extends Fragment
{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        /*ViewPager is the container for the pages*/
        ViewPager viewPager = getView().findViewById(R.id.viewpager);

        ViewPagerAdapter tabPager = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(tabPager);

        // Display a tab for each Fragment displayed in ViewPager.
        TabLayout tabLayout = getView().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        for (Fragment fragment : getChildFragmentManager().getFragments())
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*Handles which fragment is displayed inside the ViewPager*/
    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private String tabTitles[] = new String[] { "PHOTOS", "DETAILS", "PREFERENCES"};

        ViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public int getCount()
        {
            return 3; //Set the number of tabs you want here
        }

        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new PhotosFragment(getContext());
                case 1:
                    return new DetailsFragment(getContext());
                case 2:
                    return new PreferencesFragment();
            }
            return null;
        }
        public CharSequence getPageTitle(int position)
        {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}