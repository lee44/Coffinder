package com.apps.jlee.boginder.Activities;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ProfileSliderActivity extends AppCompatActivity
{
    private ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    private ViewPager viewPager;
    static ArrayList<String> profileImageUrlArray;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_page);

        profileImageUrlArray = getIntent().getStringArrayListExtra("url_arraylist");

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
    }

    class ImageFragmentPagerAdapter extends FragmentPagerAdapter
    {
        public ImageFragmentPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public int getCount()
        {
            for(int i = 0; i < profileImageUrlArray.size(); i++)
            {
                if(profileImageUrlArray.get(i).equals("Default"))
                {
                    return i;
                }
            }
            return profileImageUrlArray.size();
        }

        @Override
        public Fragment getItem(int position)
        {
            SwipeFragment fragment = new SwipeFragment();
            return fragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View swipeView = inflater.inflate(R.layout.fragment_swipe, container, false);
            ImageView imageView = swipeView.findViewById(R.id.imageView);

            Bundle bundle = getArguments();
            int position = bundle.getInt("position");

            Glide.with(getContext()).load(profileImageUrlArray.get(position)).into(imageView);

            return swipeView;
        }

        public SwipeFragment newInstance(int position)
        {
            SwipeFragment swipeFragment = new SwipeFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }
}
