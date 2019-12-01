package com.apps.jlee.boginder.Fragments;

import android.content.Context;
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
import java.util.Map;

public class DateFragment extends Fragment
{
    private List<Cards> cardsList;
    private CardsAdapter cardAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String user_gender, opposite_gender, currentUser_id;

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

        checkUserGender();

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

    public void checkUserGender()
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
                    if(dataSnapshot.child("Gender").getValue().toString().equals("Male"))
                    {
                        user_gender = "Male";
                        opposite_gender = "Female";
                    }
                    else
                    {
                        user_gender = "Female";
                        opposite_gender = "Male";
                    }

                    getOppositeGender();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    public void getOppositeGender()
    {
        DatabaseReference oppositeGenderDB = databaseReference;

        oppositeGenderDB.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.exists() && !dataSnapshot.child("Connections/Nope/").hasChild(currentUser_id) &&
                            !dataSnapshot.child("Connections/Yes/").hasChild(currentUser_id) &&
                            dataSnapshot.child("Gender").getValue().toString().equals(opposite_gender))
                    {
                        cardsList.add(new Cards(dataSnapshot.getKey(),dataSnapshot.child("Name").getValue().toString(),dataSnapshot.child("ProfileImageUrl").getValue().toString()));
                        cardAdapter.notifyDataSetChanged();
                    }

//                    Log.v("Lakers",dataSnapshot.child("Gender").getValue().toString());
//                    for(DataSnapshot datashot : dataSnapshot.getChildren())
//                    {
//                        //if(datashot.getKey().equals("Gender"))
//                            Log.v("Lakers",datashot.getValue().toString());
//                    }
                    //Log.v("Lakers",dataSnapshot.getChildrenCount()+"");
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
}
