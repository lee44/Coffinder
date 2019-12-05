package com.apps.jlee.boginder.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.jlee.boginder.R;


public class PhotosFragment extends Fragment
{
    public PhotosFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }

}
