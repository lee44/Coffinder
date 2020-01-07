package com.apps.jlee.boginder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.apps.jlee.boginder.R;

import androidx.fragment.app.Fragment;

public class ProgressFragment extends Fragment
{
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        progressBar = view.findViewById(R.id.progressBar);

        return view;
    }
}
