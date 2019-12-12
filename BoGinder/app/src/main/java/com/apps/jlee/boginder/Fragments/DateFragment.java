package com.apps.jlee.boginder.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apps.jlee.boginder.Adapter.CardsAdapter;
import com.apps.jlee.boginder.Models.Cards;
import com.apps.jlee.boginder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class DateFragment extends Fragment
{
    private List<Cards> cardsList;
    private CardsAdapter cardAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String user_gender, orientation, currentUser_id;

    @BindView(R.id.frame)
    SwipeFlingAdapterView flingContainer;

    private Context context;

    public DateFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_date, container, false);

        ButterKnife.bind(this,view);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        cardsList = new ArrayList<Cards>();
        cardAdapter = new CardsAdapter(context,R.layout.card,cardsList);

        checkUserOrientation();

        Log.v("Lakers",getArguments().getDouble("Latitude")+", "+getArguments().getDouble("Longitude"));
        flingContainer.setAdapter(cardAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener()
        {
            @Override
            public void removeFirstObjectInAdapter()
            {

            }

            @Override
            public void onLeftCardExit(Object dataObject)
            {
                Cards card = (Cards) dataObject;
                String user_id = card.getUser_id();
                databaseReference.child("/"+user_id+"/Connections/Nope/").child(currentUser_id).setValue(true);
                makeToast(context, "Left!");
            }

            @Override
            public void onRightCardExit(Object dataObject)
            {
                Cards card = (Cards) dataObject;
                String user_id = card.getUser_id();
                databaseReference.child("/"+user_id+"/Connections/Yes/").child(currentUser_id).setValue(true);
                isConnectionMatch(user_id);
                makeToast(context, "Right!");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter)
            {

            }

            @Override
            public void onScroll(float scrollProgressPercent)
            {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject)
            {
                makeToast(context, "Clicked!");
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

    static void makeToast(Context ctx, String s)
    {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    public void checkUserOrientation()
    {
        DatabaseReference userDB = databaseReference.child(currentUser_id);

        userDB.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            /*Callback method onChildAdded is triggered once for each existing child and for every new child added*/
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getKey().equals(currentUser_id))
                {
                    orientation = dataSnapshot.child("Orientation").getValue().toString();
                    getPotentialMatches();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    public void getPotentialMatches()
    {
        DatabaseReference oppositeGenderDB = databaseReference;

        oppositeGenderDB.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                    if(!dataSnapshot.child("Connections/Nope/").hasChild(currentUser_id) && !dataSnapshot.child("Connections/Yes/").hasChild(currentUser_id))
                    {
                        SharedPreferences sp = getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        if (sp.getInt("Age_Low",18) <= Integer.valueOf(dataSnapshot.child("Age").getValue().toString()) &&
                            sp.getInt("Age_High",50) >= Integer.valueOf(dataSnapshot.child("Age").getValue().toString()) &&
                            getHeight(sp.getInt("Height_Low",48)).compareTo(dataSnapshot.child("Height").getValue().toString()) <= 0 &&
                            getHeight(sp.getInt("Height_High",84)).compareTo(dataSnapshot.child("Height").getValue().toString()) >= 0)
                        {
                            if (orientation.equals("Men") && dataSnapshot.child("Gender").getValue().toString().equals("Male"))
                            {
                                cardsList.add(new Cards(dataSnapshot.getKey(), dataSnapshot.child("Name").getValue().toString(), dataSnapshot.child("Age").getValue().toString(),
                                        dataSnapshot.child("Height").getValue().toString(), dataSnapshot.child("City").getValue().toString(),
                                        dataSnapshot.child("ProfileImageUrl").getValue().toString()));
                            }
                            else if (orientation.equals("Women") && dataSnapshot.child("Gender").getValue().toString().equals("Female"))
                            {
                                cardsList.add(new Cards(dataSnapshot.getKey(), dataSnapshot.child("Name").getValue().toString(), dataSnapshot.child("Age").getValue().toString(),
                                        dataSnapshot.child("Height").getValue().toString(), dataSnapshot.child("City").getValue().toString(),
                                        dataSnapshot.child("ProfileImageUrl").getValue().toString()));
                            }
                            cardAdapter.notifyDataSetChanged();
                        }
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot){}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    public String getHeight(int inches)
    {
        int feet = inches/12;
        inches = inches%12;

        return feet+"'"+inches+'"';
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
