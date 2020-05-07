package com.apps.jlee.coffinder.Fragments;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.jlee.coffinder.Adapters.MatchesAdapter;
import com.apps.jlee.coffinder.Adapters.MessagesAdapter;
import com.apps.jlee.coffinder.Interfaces.MatchInterface;
import com.apps.jlee.coffinder.Models.Match;
import com.apps.jlee.coffinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MatchFragment extends Fragment implements MatchInterface.MatchCallback
{
    @BindView(R.id.matches_recycleView) RecyclerView matches_recycleView;
    @BindView(R.id.messages_recycleView) RecyclerView messages_recycleView;
    @BindView(R.id.new_matches) TextView new_matches;
    @BindView(R.id.no_matches) TextView no_matches;
    @BindView(R.id.messages) TextView messages;
    @BindView(R.id.no_messages) TextView no_messages;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private Context context;
    private ArrayList<Match> matches_list, last_message_list;
    private MatchesAdapter matchesAdapter;
    private MessagesAdapter messagesAdapter;
    private String current_user_id;
    private long childrencount;

    public MatchFragment(Context context)
    {
        this.context = context;
        matches_list = new ArrayList<>();
        last_message_list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_match, container, false);

        ButterKnife.bind(this,view);

        toggleUI(true);

        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getMatchUserID();

        matches_recycleView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        matchesAdapter = new MatchesAdapter(matches_list,context);
        matches_recycleView.setAdapter(matchesAdapter);

        messages_recycleView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        messagesAdapter = new MessagesAdapter(last_message_list,context);
        messages_recycleView.setAdapter(messagesAdapter);

        return view;
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
                    childrencount = dataSnapshot.getChildrenCount();
                    for(DataSnapshot match : dataSnapshot.getChildren())
                    {
                        fetchMatchInformation(match.getKey(),match.child("chat_id").getValue().toString());
                    }
                }
                else
                {
                    loadUI();
                    no_matches.setVisibility(View.VISIBLE);
                    no_messages.setVisibility(View.VISIBLE);
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
                    String user_id = dataSnapshot.getKey(),name = "";
                    ArrayList<String> profileImageUrlArray = new ArrayList<>();

                    if(dataSnapshot.child("Name").getValue() != null)
                    {
                        name = dataSnapshot.child("Name").getValue().toString();
                    }

                    long childrenSize = dataSnapshot.child("ProfileImageUrl").getChildrenCount();
                    if(childrenSize != 0)
                    {
                        for (DataSnapshot child : dataSnapshot.child("ProfileImageUrl").getChildren())
                        {
                            profileImageUrlArray.add(child.getValue().toString());
                        }
                    }
                    else
                    {
                        //else is needed because if the child("ProfileImageUrl") has only one value, it wont be a child
                        profileImageUrlArray.add(dataSnapshot.child("ProfileImageUrl").getValue().toString());
                    }

                    matches_list.add(new Match(user_id,name,profileImageUrlArray,chat_id,"",""));
                    last_message_list.add(new Match(user_id,name,profileImageUrlArray,chat_id,"",""));
                    if(matches_list.size() != 0)
                    {
                        no_matches.setVisibility(View.GONE);
                        matches_recycleView.setVisibility(View.VISIBLE);

                        no_messages.setVisibility(View.GONE);
                        messages_recycleView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        no_matches.setVisibility(View.VISIBLE);
                        matches_recycleView.setVisibility(View.GONE);

                        no_messages.setVisibility(View.VISIBLE);
                        messages_recycleView.setVisibility(View.GONE);
                    }
                    matchesAdapter.notifyDataSetChanged();
                    fetchLastMessage(chat_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    private void fetchLastMessage(String chat_id)
    {
        Query lastQuery = databaseReference.child("Chat").child(chat_id).orderByKey().limitToLast(1);
        int position = last_message_list.size()-1;

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot m : dataSnapshot.getChildren())
                    {
                        last_message_list.get(position).setMessage(m.child("Message").getValue().toString());
                        if(m.child("Sender_ID").getValue().toString().equals(current_user_id))
                            last_message_list.get(position).setMessage_direction("Sent");
                    }
                    messagesAdapter.notifyDataSetChanged();
                    if(--childrencount == 0)
                    {
                        removeEmptyLastMessages();
                        loadUI();
                    }
                }
                else if(--childrencount == 0)
                {
                    removeEmptyLastMessages();
                    loadUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }

    public void removeEmptyLastMessages()
    {
        for(int i = 0; i < last_message_list.size(); i ++)
        {
            if(last_message_list.get(i).getMessage().isEmpty())
            {
                last_message_list.remove(i);
                messagesAdapter.notifyItemRemoved(i);
            }
        }
    }

    //Callback method for when the data is finished loading
    @Override
    public void loadUI()
    {
        toggleUI(false);
    }

    public void toggleUI(boolean toggle)
    {
        if(toggle)
        {
            progressBar.setVisibility(View.VISIBLE);
            matches_recycleView.setVisibility(View.GONE);
            messages_recycleView.setVisibility(View.GONE);
            new_matches.setVisibility(View.GONE);
            messages.setVisibility(View.GONE);
            no_matches.setVisibility(View.GONE);
            no_messages.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            matches_recycleView.setVisibility(View.VISIBLE);
            new_matches.setVisibility(View.VISIBLE);
            messages_recycleView.setVisibility(View.VISIBLE);
            messages.setVisibility(View.VISIBLE);
        }
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
        last_message_list.clear();
    }
}
