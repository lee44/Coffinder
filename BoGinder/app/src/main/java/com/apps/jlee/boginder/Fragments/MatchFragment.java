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

import com.apps.jlee.boginder.Adapters.MatchesAdapter;
import com.apps.jlee.boginder.Adapters.MessagesAdapter;
import com.apps.jlee.boginder.Models.Match;
import com.apps.jlee.boginder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchFragment extends Fragment
{
    @BindView(R.id.matches_recycleView)
    RecyclerView matches_recycleView;
    @BindView(R.id.messages_recycleView)
    RecyclerView messages_recycleView;
    @BindView(R.id.No_Matches)
    TextView No_Matches;
    @BindView(R.id.No_Messages)
    TextView No_Messages;

    DatabaseReference databaseReference;
    private Context context;
    private ArrayList<Match> matches_list;
    private MatchesAdapter matchesAdapter;
    private MessagesAdapter messagesAdapter;
    private String current_user_id;

    public MatchFragment(Context context)
    {
        this.context = context;

        matches_list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_match, container, false);

        ButterKnife.bind(this,view);

        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getMatchUserID();

        matches_recycleView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        matchesAdapter = new MatchesAdapter(getMatches(),context);
        matches_recycleView.setAdapter(matchesAdapter);

        messages_recycleView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        messagesAdapter = new MessagesAdapter(getMatches(),context);
        messages_recycleView.setAdapter(messagesAdapter);

        return view;
    }

    private List<Match> getMatches()
    {
        return matches_list;
    }

    /**
     * Grabs all the matches the user has
     */
    private void getMatchUserID()
    {
        DatabaseReference matchDB = databaseReference.child("Users/"+current_user_id+"/Connections/Match");
        //Use ListenerForSingleValueEvent for grabbing a single value
        matchDB.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot match : dataSnapshot.getChildren())
                    {
                        fetchMatchInformation(match.getKey(),match.child("chat_id").getValue().toString());
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
    private void fetchMatchInformation(String key, final String chat_id)
    {
        DatabaseReference matchInfoDB = databaseReference.child("Users/"+key);
        matchInfoDB.addListenerForSingleValueEvent(new ValueEventListener()
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

                    matches_list.add(new Match(user_id,name,profileImageUrl,chat_id,"",""));
                    if(matches_list.size() != 0)
                    {
                        No_Matches.setVisibility(View.GONE);
                        matches_recycleView.setVisibility(View.VISIBLE);

                        No_Messages.setVisibility(View.GONE);
                        messages_recycleView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        No_Matches.setVisibility(View.VISIBLE);
                        matches_recycleView.setVisibility(View.GONE);

                        No_Messages.setVisibility(View.VISIBLE);
                        messages_recycleView.setVisibility(View.GONE);
                    }
                    matchesAdapter.notifyDataSetChanged();
                    fetchLastMessage(chat_id,0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    private void fetchLastMessage(final String chat_id, final int position)
    {
        Query lastQuery = databaseReference.child("Chat").child(chat_id).orderByKey().limitToLast(1);

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot m : dataSnapshot.getChildren())
                    {
                        matches_list.get(position).setMessage(m.child("Message").getValue().toString());
                        if(m.child("Sender_ID").getValue().toString().equals(current_user_id))
                            matches_list.get(position).setMessage_direction("Sent");
                    }
                    messagesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //Log.v("Lakers","onStart");

    }
    @Override
    public void onResume()
    {
        super.onResume();
        //Log.v("Lakers","onResume");

    }
    @Override
    public void onPause()
    {
        super.onPause();
        //Log.v("Lakers","onPause");

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Log.v("Lakers","onDestroy");
        matches_list.clear();
    }
}
