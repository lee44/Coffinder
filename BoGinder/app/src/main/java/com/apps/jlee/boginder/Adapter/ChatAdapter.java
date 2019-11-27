package com.apps.jlee.boginder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jlee.boginder.Activities.ChatActivity;
import com.apps.jlee.boginder.Chat;
import com.apps.jlee.boginder.Matches;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{
    List<Chat> chat;
    private Context context;

    public ChatAdapter(List<Chat> chat, Context context)
    {
        this.chat = chat;
        this.context = context;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_chat, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return chat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
