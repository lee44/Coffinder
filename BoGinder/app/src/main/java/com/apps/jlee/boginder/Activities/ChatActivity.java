package com.apps.jlee.boginder.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.apps.jlee.boginder.Adapter.ChatAdapter;
import com.apps.jlee.boginder.Objects.Chat;
import com.apps.jlee.boginder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity
{
    @BindView(R.id.chat_recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.chat_editText)
    EditText editText;
    @BindView(R.id.chat_send_button)
    Button button;

    DatabaseReference databaseReference, databaseChat;
    private ArrayList<Chat> resultChats = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private String current_user_id, match_id, chat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        match_id = getIntent().getExtras().getString("MatchID");
        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users/"+current_user_id+"/Connections/Matches/"+match_id+"/chat_id");
        databaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatID();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(getChats(),this);
        recyclerView.setAdapter(chatAdapter);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendMessage();
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
            map.put("created_by", current_user_id);
            map.put("text",sendMessageText);
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
                    String created_by = null;

                    if(dataSnapshot.child("text").getValue().toString() != null)
                    {
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("created_by").getValue().toString() != null)
                    {
                        created_by = dataSnapshot.child("created_by").getValue().toString();
                    }
                    if(message != null && created_by != null)
                    {
                        Boolean currentUserBoolean = false;
                        if (created_by.equals(current_user_id))
                        {
                            currentUserBoolean = true;
                        }
                        Chat newMessage = new Chat(message, currentUserBoolean);
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
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
