package com.apps.jlee.boginder.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.jlee.boginder.Adapter.MatchesAdapter;
import com.apps.jlee.boginder.Models.Matches;
import com.apps.jlee.boginder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchFragment extends Fragment
{
    @BindView(R.id.matches_recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.No_Matches)
    TextView textView;

    private Context context;
    private ArrayList<Matches> resultMatches = new ArrayList<>();
    private MatchesAdapter matchesAdapter;
    private String current_user_id;

    public MatchFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_match, container, false);

        ButterKnife.bind(this,view);

        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        getMatchUserID();

        recyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        matchesAdapter = new MatchesAdapter(getMatches(),context);
        recyclerView.setAdapter(matchesAdapter);

        return view;
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
                    if(resultMatches.size() != 0)
                    {
                        textView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        textView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    matchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }
}
