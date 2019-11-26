package com.apps.jlee.boginder.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import android.os.Bundle;

import com.apps.jlee.boginder.R;

public class MatchesActivity extends AppCompatActivity
{
    @BindView(R.id.matches_recycleView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

    }
}
