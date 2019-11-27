package com.apps.jlee.boginder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;

import com.apps.jlee.boginder.Adapter.MatchesAdapter;
import com.apps.jlee.boginder.Matches;
import com.apps.jlee.boginder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity
{
    @BindView(R.id.matches_recycleView)
    RecyclerView recyclerView;

    private ArrayList<Matches> resultMatches = new ArrayList<>();
    private MatchesAdapter matchesAdapter;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        ButterKnife.bind(this);

        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        getMatchUserID();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        matchesAdapter = new MatchesAdapter(getMatches(),this);
        recyclerView.setAdapter(matchesAdapter);
    }

    private List<Matches> getMatches()
    {
        return resultMatches;
    }

    /**
     * Grabs all the matches the user has
     */
    private void getMatchUserID()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users/"+current_user_id+"/Connections/Matches");
        //Use ListenerForSingleValueEvent for grabbing a single value
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot match : dataSnapshot.getChildren())
                    {
                        fetchMatchInformation(match.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    /**
     * Fetches the information for each match
     * @param key
     */
    private void fetchMatchInformation(String key)
    {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users/"+key);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String user_id = dataSnapshot.getKey();
                    String name = "",profileImageUrl = "";

                    if(dataSnapshot.child("Name").getValue() != null)
                    {
                        name = dataSnapshot.child("Name").getValue().toString();
                    }
                    if(dataSnapshot.child("ProfileImageUrl").getValue() != null)
                    {
                        profileImageUrl = dataSnapshot.child("ProfileImageUrl").getValue().toString();
                    }

                    resultMatches.add(new Matches(user_id,name,profileImageUrl));
                    matchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }
}
