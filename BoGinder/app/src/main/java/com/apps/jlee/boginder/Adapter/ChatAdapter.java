package com.apps.jlee.boginder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.jlee.boginder.Chat;
import com.apps.jlee.boginder.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{
    List<Chat> chat;
    private Context context;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public ChatAdapter(List<Chat> chat, Context context)
    {
        this.chat = chat;
        this.context = context;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;

        if(viewType == VIEW_TYPE_MESSAGE_SENT)
            view = inflater.inflate(R.layout.item_chat_send, parent, false);
        else if(viewType == VIEW_TYPE_MESSAGE_RECEIVED)
            view = inflater.inflate(R.layout.item_chat_receive, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position)
    {
        if(chat.get(position).getCurrentUser())
        {
            holder.senderTextView.setText(chat.get(position).getMessage());
        }
        else
        {
            holder.receiver_TextView.setText(chat.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount()
    {
        return chat.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        Chat message = chat.get(position);

        if (message.getCurrentUser())
        {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else
        {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView receiver_TextView, senderTextView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            receiver_TextView = itemView.findViewById(R.id.receiver_textView);
            senderTextView = itemView.findViewById(R.id.sender_textView);
        }
    }
}
