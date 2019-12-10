package com.apps.jlee.boginder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.apps.jlee.boginder.Fragments.AccountFragment;
import com.apps.jlee.boginder.Fragments.MatchFragment;
import com.apps.jlee.boginder.Fragments.DateFragment;
import com.apps.jlee.boginder.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final AccountFragment accountFragment = new AccountFragment();
        final MatchFragment matchFragment = new MatchFragment(this);
        final DateFragment dateFragment = new DateFragment(this);

        setFragment(dateFragment);
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

    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        //Intent intent = new Intent(this,LoginRegisterActivity.class);
        //startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideToolbarItems(boolean hide)
    {
        if(hide)
            for(int i = 0; i < toolbar.getMenu().size(); i++)
            {
                toolbar.getMenu().getItem(i).setVisible(false);
            }
        else
            for(int i = 0; i < toolbar.getMenu().size(); i++)
            {
                toolbar.getMenu().getItem(i).setVisible(true);
            }
    }
}
