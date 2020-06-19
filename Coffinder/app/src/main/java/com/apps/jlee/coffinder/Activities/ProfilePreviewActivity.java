package com.apps.jlee.coffinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.apps.jlee.coffinder.Fragments.ProfilePreviewFragment;
import com.apps.jlee.coffinder.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfilePreviewActivity extends AppCompatActivity
{
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_preview);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        toolbar.setVisibility(View.GONE);

        ProfilePreviewFragment profilePreviewFragment = new ProfilePreviewFragment(this);

        if(getIntent().getExtras() != null)
        {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", getIntent().getExtras().getString("user_id"));
            profilePreviewFragment.setArguments(bundle);
        }

        setFragment(profilePreviewFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        for (Fragment fragment : getSupportFragmentManager().getFragments())
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.profile_preview_main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
