package com.apps.jlee.boginder.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.apps.jlee.boginder.Cards;
import com.apps.jlee.boginder.Adapter.CardsAdapter;
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

public class MainActivity extends AppCompatActivity
{
    private List<Cards> cardsList;
    private CardsAdapter cardAdapter;
    private SwipeFlingAdapterView flingContainer;
    private FirebaseAuth firebaseAuth;
    private Button sign_out_button, settings_button;
    private DatabaseReference databaseReference;
    private int i;
    private String user_gender, opposite_gender, currentUser_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        cardsList = new ArrayList<Cards>();
        checkUserGender();

        cardAdapter = new CardsAdapter(this,R.layout.card,cardsList);
        flingContainer = findViewById(R.id.frame);
        sign_out_button = findViewById(R.id.sign_out_button);
        settings_button = findViewById(R.id.settings_button);

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
                makeToast(MainActivity.this, "Left!");
            }

            @Override
            public void onRightCardExit(Object dataObject)
            {
                Cards card = (Cards) dataObject;
                String user_id = card.getUser_id();
                databaseReference.child("/"+user_id+"/Connections/Yes/").child(currentUser_id).setValue(true);
                isConnectionMatch(user_id);
                makeToast(MainActivity.this, "Right!");
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
                makeToast(MainActivity.this, "Clicked!");
            }
        });

        sign_out_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                logoutUser(view);
            }
        });

        settings_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("Gender",user_gender);
                startActivity(intent);
            }
        });
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
                    makeToast(MainActivity.this, "New Connection");

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

    public void logoutUser(View view)
    {
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginorRegisterActivity.class);
        startActivity(intent);
        finish();
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
                if(dataSnapshot.exists() && !dataSnapshot.child("Connections/Nope/").hasChild(currentUser_id) &&
                   !dataSnapshot.child("Connections/Yes/").hasChild(currentUser_id) &&
                   dataSnapshot.child("Gender").getValue().toString().equals(opposite_gender))
                {
                    cardsList.add(new Cards(dataSnapshot.getKey(),dataSnapshot.child("Name").getValue().toString(),dataSnapshot.child("ProfileImageUrl").getValue().toString()));
                    cardAdapter.notifyDataSetChanged();
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

    public void loadMatchesActivity(View view)
    {
        Intent intent = new Intent(MainActivity.this,MatchesActivity.class);
        startActivity(intent);
    }
}
