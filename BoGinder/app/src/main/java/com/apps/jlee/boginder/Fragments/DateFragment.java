package com.apps.jlee.boginder.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.jlee.boginder.Adapter.CardsAdapter;
import com.apps.jlee.boginder.Models.Cards;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateFragment extends Fragment
{
    @BindView(R.id.potential_match_pic)
    ImageView image;
    @BindView(R.id.potential_match_name)
    TextView name;
    @BindView(R.id.potential_match_age)
    TextView age;
    @BindView(R.id.potential_match_height)
    TextView height;
    @BindView(R.id.potential_match_distance)
    TextView distance;
    @BindView(R.id.like_floatingActionButton)
    FloatingActionButton likeButton;
    @BindView(R.id.nope_floatingActionButton)
    FloatingActionButton nopeButton;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Context context;
    private ArrayList<Cards> cardsList;
    private String user_gender, orientation, currentUser_id;

    public DateFragment(Context context, ArrayList<Cards> cardsList)
    {
        this.context = context;
        this.cardsList = cardsList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_date, container, false);

        ButterKnife.bind(this,view);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        if(cardsList.get(0).getProfileImageUrl().equals("Default"))
        {
            Glide.with(getContext()).load(R.mipmap.ic_launcher).into(image);
        }
        else
            Glide.with(getContext()).load(cardsList.get(0).getProfileImageUrl()).into(image);

        name.setText(cardsList.get(0).getName());
        age.setText(cardsList.get(0).getAge());
        height.setText(cardsList.get(0).getHeight());
        distance.setText(cardsList.get(0).getDistance());

        likeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String user_id = cardsList.get(0).getUser_id();
                databaseReference.child("/"+user_id+"/Connections/Yes/").child(currentUser_id).setValue(true);
                isConnectionMatch(user_id);
                makeToast(context, "Right!");

                refresh();
            }
        });

        nopeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String user_id = cardsList.get(0).getUser_id();
                databaseReference.child("/"+user_id+"/Connections/Nope/").child(currentUser_id).setValue(true);
                makeToast(context, "Left!");

                refresh();
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
                    databaseReference.child(dataSnapshot.getKey()).child("Connections/Matches/"+currentUser_id+"/chat_id").setValue(key);
                    databaseReference.child(currentUser_id).child("Connections/Matches/"+dataSnapshot.getKey()+"/chat_id").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    public void refresh()
    {
        cardsList.remove(0);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(DateFragment.this);
        ft.attach(DateFragment.this);
        ft.commit();
    }

    static void makeToast(Context ctx, String s)
    {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
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
