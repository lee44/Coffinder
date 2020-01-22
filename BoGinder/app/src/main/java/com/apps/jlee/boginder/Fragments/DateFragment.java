package com.apps.jlee.boginder.Fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.jlee.boginder.Models.Card;
import com.apps.jlee.boginder.R;
import com.apps.jlee.boginder.Service.Constants;
import com.apps.jlee.boginder.Service.FetchIntentService;
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
    @BindView(R.id.pic) ImageView image;
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

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Context context;
    private ArrayList<Card> cardsList;
    private ProfileResultReceiver resultReceiver;
    private Location lastLocation;
    private String currentUser_id;

    public DateFragment(Context context, ArrayList<Card> cardsList, Location lastLocation)
    {
        this.context = context;
        this.cardsList = cardsList;
        this.lastLocation = lastLocation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_date, container, false);
        ButterKnife.bind(this,view);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                progressBar.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.INVISIBLE);
                startIntentService();
            }
        });

        if(cardsList.size() > 0)
        {
            if (cardsList.get(0).getProfileImageUrl().equals("Default"))
            {
                Glide.with(getContext()).load(R.mipmap.ic_launcher).into(image);
            }
            else
                Glide.with(getContext()).load(cardsList.get(0).getProfileImageUrl()).into(image);

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
                    no_data.setVisibility(View.INVISIBLE);
                    toggleUI(false);
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.INVISIBLE);
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

    public void toggleUI(boolean isToggle)
    {
        if(isToggle)
        {
            progressBar.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
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
            progressBar.setVisibility(View.INVISIBLE);
            image.setVisibility(View.VISIBLE);
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
