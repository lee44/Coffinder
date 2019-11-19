package com.apps.jlee.boginder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private SwipeFlingAdapterView flingContainer;
    private FirebaseAuth firebaseAuth;
    private Button sign_out_button;
    private int i;
    private String user_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        al = new ArrayList<>();
        checkUserGender();

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item, R.id.helloText, al);
        flingContainer = findViewById(R.id.frame);
        sign_out_button = findViewById(R.id.sign_out_button);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener()
        {
            @Override
            public void removeFirstObjectInAdapter()
            {

            }

            @Override
            public void onLeftCardExit(Object dataObject)
            {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                makeToast(MainActivity.this, "Left!");
            }

            @Override
            public void onRightCardExit(Object dataObject)
            {
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
    }

    static void makeToast(Context ctx, String s)
    {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    public void logoutUser(View view)
    {
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this,LoginorRegisterActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void checkUserGender()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference maleDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        DatabaseReference femaleDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Female");

        maleDB.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.getKey().equals(firebaseUser.getUid()))
                {
                    user_gender = "Male";
                    getOppositeGender();
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

        femaleDB.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.getKey().equals(firebaseUser.getUid()))
                {
                    user_gender = "Female";
                    getOppositeGender();
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

    public void getOppositeGender()
    {
        DatabaseReference oppositeGenderDB = FirebaseDatabase.getInstance().getReference().child("Users").child(user_gender == "Male" ? "Female" : "Male");

        oppositeGenderDB.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    al.add(dataSnapshot.child("name").getValue().toString());
                    arrayAdapter.notifyDataSetChanged();
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
