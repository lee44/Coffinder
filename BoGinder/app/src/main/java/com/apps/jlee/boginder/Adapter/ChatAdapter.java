package com.apps.jlee.boginder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
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
        View view = inflater.inflate(R.layout.item_chat, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position)
    {
        holder.chatTextView.setText(chat.get(position).getMessage());
        if(chat.get(position).getCurrentUser())
        {
            holder.chatTextView.setGravity(Gravity.END);
            holder.chatTextView.setBackgroundColor(Color.YELLOW);
        }
        else
        {
            holder.chatTextView.setGravity(Gravity.START);
            holder.chatTextView.setBackgroundColor(Color.RED);
        }
    }

    @Override
    public int getItemCount()
    {
        return chat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView chatTextView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            chatTextView = itemView.findViewById(R.id.chat_textView);
        }
    }
}
