package com.apps.jlee.coffinder.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.apps.jlee.coffinder.Adapters.ChatAdapter;
import com.apps.jlee.coffinder.Models.Chat;
import com.apps.jlee.coffinder.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity
{
    @BindView(R.id.chat_toolbar)
    Toolbar toolbar;
    @BindView(R.id.messages_profile)
    ImageView messages_profile_imageView;
    @BindView(R.id.chat_recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.chat_editText)
    EditText editText;
    @BindView(R.id.chat_send_button)
    Button button;
    @BindView(R.id.back_button)
    ImageButton back_button;

    DatabaseReference databaseReference, databaseChat;
    private ArrayList<Chat> resultChats = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private String current_user_id, match_id, chat_id, profileImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        match_id = getIntent().getExtras().getString("MatchID");
        profileImageURL = getIntent().getExtras().getString("ProfileImageURL");
        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users/"+current_user_id+"/Connections/Match/"+match_id+"/chat_id");
        databaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatID();

        chatAdapter = new ChatAdapter(getChats(),this);
        recyclerView.setAdapter(chatAdapter);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getBaseContext());
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

        if (profileImageURL.equals("Default"))
        {
            Glide.with(this).load(R.mipmap.ic_launcher).into(messages_profile_imageView);
        }
        else
            Glide.with(this).load(profileImageURL).into(messages_profile_imageView);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendMessage();
            }
        });

        messages_profile_imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ChatActivity.this,ProfilePreviewActivity.class);
                intent.putExtra("user_id",match_id);
                startActivity(intent);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    private void sendMessage()
    {
        String sendMessageText = editText.getText().toString();

        if(!sendMessageText.isEmpty())
        {
            DatabaseReference newMessage = databaseChat.push();

            Map map = new HashMap<>();
            map.put("Sender_ID", current_user_id);
            map.put("Message",sendMessageText);
            map.put("Receiver_ID",match_id);

            Date currentTime = Calendar.getInstance().getTime();
            map.put("Datetime", android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss aa", currentTime));
            newMessage.setValue(map);
        }
        editText.setText(null);
    }

    private void getChatID()
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    chat_id = dataSnapshot.getValue().toString();
                    databaseChat = databaseChat.child(chat_id);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    private void getChatMessages()
    {
        databaseChat.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    String message = null;
                    String sender_id = null;
                    String receiver_id = null;
                    String datetime = null;

                    if(dataSnapshot.child("Message").getValue().toString() != null)
                    {
                        message = dataSnapshot.child("Message").getValue().toString();
                    }
                    if(dataSnapshot.child("Sender_ID").getValue().toString() != null)
                    {
                        sender_id = dataSnapshot.child("Sender_ID").getValue().toString();
                    }
                    if(dataSnapshot.child("Receiver_ID").getValue().toString() != null)
                    {
                        receiver_id = dataSnapshot.child("Receiver_ID").getValue().toString();
                    }
                    if(dataSnapshot.child("Datetime").getValue().toString() != null)
                    {
                        datetime = dataSnapshot.child("Datetime").getValue().toString();
                    }
                    if(message != null && sender_id != null && receiver_id != null && datetime != null)
                    {
                        Chat newMessage = new Chat(message, receiver_id, sender_id,datetime);
                        resultChats.add(newMessage);
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}

            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot){}

            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}

            @Override public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    private List<Chat> getChats()
    {
        return resultChats;
    }

    @Override
    public void onBackPressed()
    {
        finish();
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

        try
        {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        //Log.v("Lakers","DateFragment resume");

    }
    @Override
    public void onPause()
    {
        super.onPause();
        FirebaseInstanceId.getInstance().getToken();
        //Log.v("Lakers","DateFragment paused");

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Log.v("Lakers","DateFragment destroyed");
    }
}
