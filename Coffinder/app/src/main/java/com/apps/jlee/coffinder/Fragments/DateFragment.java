package com.apps.jlee.coffinder.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.jlee.coffinder.Activities.ProfileSliderActivity;
import com.apps.jlee.coffinder.Models.Card;
import com.apps.jlee.coffinder.R;
import com.apps.jlee.coffinder.Service.Constants;
import com.apps.jlee.coffinder.Service.FetchIntentService;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DateFragment extends Fragment
{
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.age_city) TextView age_city;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.height_header) TextView height_header;
    @BindView(R.id.height) TextView height;
    @BindView(R.id.imageView1) ImageView job_image;
    @BindView(R.id.job) TextView job;
    @BindView(R.id.imageView2) ImageView school_image;
    @BindView(R.id.school) TextView school;
    @BindView(R.id.description_header) TextView description_header;
    @BindView(R.id.description) TextView description;
    @BindView(R.id.religion_header) TextView religion_header;
    @BindView(R.id.religion) TextView religion;
    @BindView(R.id.ethnicity_header) TextView ethnicity_header;
    @BindView(R.id.ethnicity) TextView ethnicity;
    @BindView(R.id.like_floatingActionButton) FloatingActionButton likeButton;
    @BindView(R.id.nope_floatingActionButton) FloatingActionButton nopeButton;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.no_data) TextView no_data;
    @BindView(R.id.swiperefreshlayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.SliderDots) LinearLayout sliderDotspanel;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProfileResultReceiver resultReceiver;
    private ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    private ArrayList<Card> cardsList;
    static ArrayList<String> profileImageUrlArray;
    private Location lastLocation;
    private Context context;
    private String currentUser_id;
    private float x, y;
    private final int THRESHOLD = 10;
    private int dotscount;
    private ImageView[] dots;

    public DateFragment(Context context, ArrayList<Card> cardsList, Location lastLocation)
    {
        this.context = context;
        this.cardsList = cardsList;
        this.lastLocation = lastLocation;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_date, container, false);
        ButterKnife.bind(this,view);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        profileImageUrlArray = cardsList.get(0).getProfileImageUrl();

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);

        /*SwipeFragment is showing the pictures so use fragment manager of parent fragment which is DateFragment
         MainActivity -> DateFragment -> SwipeFragment */
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(imageFragmentPagerAdapter);

        dotscount = imageFragmentPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++)
        {
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

        if(cardsList.size() > 0)
        {
            name.setText(cardsList.get(0).getName());
            age_city.setText(cardsList.get(0).getAge()+", "+cardsList.get(0).getCity());
            height.setText(cardsList.get(0).getHeight());
            job.setText(cardsList.get(0).getJob().length() != 0 ? cardsList.get(0).getJob() : "N/A");
            school.setText(cardsList.get(0).getSchool().length() != 0 ? cardsList.get(0).getSchool() : "N/A");
            description.setText(cardsList.get(0).getDescription().length() != 0 ? cardsList.get(0).getDescription() : "N/A");
            ethnicity.setText(cardsList.get(0).getEthnicity().length() != 0 ? cardsList.get(0).getEthnicity() : "N/A");
            religion.setText(cardsList.get(0).getReligion().length() != 0 ? cardsList.get(0).getReligion() : "N/A");

            likeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String user_id = cardsList.get(0).getUser_id();
                    databaseReference.child("/" + user_id + "/Connections/Yes/").child(currentUser_id).setValue(true);
                    isConnectionMatch(user_id);
                    cardsList.remove(0);
                    refresh();
                }
            });

            nopeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String user_id = cardsList.get(0).getUser_id();
                    databaseReference.child("/" + user_id + "/Connections/Nope/").child(currentUser_id).setValue(true);
                    cardsList.remove(0);
                    refresh();
                }
            });
        }
        else
        {
            toggleUI(true);
            startIntentService();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                progressBar.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.GONE);
                startIntentService();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position)
            {
                for(int i = 0; i< dotscount; i++)
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));

                dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        /*When user taps viewpager, a slideshow activity opens*/
        viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x = motionEvent.getX();
                        y = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x -= motionEvent.getX();
                        y -= motionEvent.getY();
                        if(Math.abs(x) <= THRESHOLD && Math.abs(y) <= THRESHOLD)
                        {
                            Intent intent = new Intent(getActivity(), ProfileSliderActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("url_arraylist",profileImageUrlArray);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        break;
                }
                return false;
            }
        });

        return view;
    }

    public void isConnectionMatch(String user_id)
    {
        //Storing the user_id of the card profile into the current user's connections tree
        DatabaseReference df = databaseReference.child(currentUser_id).child("Connections").child("Yes").child(user_id);
        df.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            /*Callback method onDataChange is triggered once for each existing child and for every new child added*/
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    makeToast(context, "New Connection");

                    //When calling push method on an empty location, it will generate a unique id
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    //dataSnapshot is pointing to the profile's user_id stored under current user's tree since the variable df was assigned the path to the profile's user_id
                    databaseReference.child(dataSnapshot.getKey()).child("Connections/Match/"+currentUser_id+"/chat_id").setValue(key);
                    databaseReference.child(currentUser_id).child("Connections/Match/"+dataSnapshot.getKey()+"/chat_id").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    public void refresh()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.detach(DateFragment.this);
        ft.attach(DateFragment.this);
        ft.commit();
    }

    public void startIntentService()
    {
        resultReceiver = new ProfileResultReceiver(new Handler());

        Intent intentProfile = new Intent(getContext(), FetchIntentService.class);
        intentProfile.putExtra("intent_type","profile");
        intentProfile.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        intentProfile.putExtra(Constants.RECEIVER, resultReceiver);

        getActivity().startService(intentProfile);
    }

    class ProfileResultReceiver extends ResultReceiver
    {
        public ProfileResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT)
            {
                ArrayList parcelable = resultData.getParcelableArrayList("Profiles");
                if(parcelable.size() > 0)
                {
                    cardsList = parcelable;
                    refresh();
                    swipeRefreshLayout.setEnabled(false);
                    no_data.setVisibility(View.GONE);
                    toggleUI(false);
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    no_data.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setEnabled(true);
                }
            }
        }
    }

    static void makeToast(Context ctx, String s)
    {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    public void toggleUI(boolean toggle)
    {
        if(toggle)
        {
            progressBar.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            age_city.setVisibility(View.GONE);
            job_image.setVisibility(View.GONE);
            job.setVisibility(View.GONE);
            school_image.setVisibility(View.GONE);
            school.setVisibility(View.GONE);
            height.setVisibility(View.GONE);
            height_header.setVisibility(View.GONE);
            description_header.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            religion_header.setVisibility(View.GONE);
            religion.setVisibility(View.GONE);
            ethnicity_header.setVisibility(View.GONE);
            ethnicity.setVisibility(View.GONE);
            likeButton.hide();
            nopeButton.hide();
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            age_city.setVisibility(View.VISIBLE);
            job_image.setVisibility(View.VISIBLE);
            job.setVisibility(View.VISIBLE);
            school_image.setVisibility(View.VISIBLE);
            school.setVisibility(View.VISIBLE);
            height.setVisibility(View.VISIBLE);
            height_header.setVisibility(View.VISIBLE);
            description_header.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            religion_header.setVisibility(View.VISIBLE);
            religion.setVisibility(View.VISIBLE);
            ethnicity_header.setVisibility(View.VISIBLE);
            ethnicity.setVisibility(View.VISIBLE);
            likeButton.show();
            nopeButton.show();
        }
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
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment
    {
        public static SwipeFragment newInstance(int position)
        {
            SwipeFragment swipeFragment = new SwipeFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("SwipeFragment_position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View swipeView = inflater.inflate(R.layout.fragment_swipe_small, container, false);
            ImageView imageView = swipeView.findViewById(R.id.imageView);

            Bundle bundle = getArguments();
            int position = bundle.getInt("SwipeFragment_position");
            Glide.with(getContext()).load(profileImageUrlArray.get(position)).into(imageView);

            return swipeView;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //Log.v("Lakers","DateFragment started");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Log.v("Lakers","DateFragment resume");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //Log.v("Lakers","DateFragment paused");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Log.v("Lakers","DateFragment destroyed");
    }
}
