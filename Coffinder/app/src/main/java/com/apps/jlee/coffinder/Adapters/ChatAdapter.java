package com.apps.jlee.coffinder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.jlee.coffinder.Models.Chat;
import com.apps.jlee.coffinder.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{
    List<Chat> chat;
    private Context context;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private String current_user_id;

    public ChatAdapter(List<Chat> chat, Context context)
    {
        this.chat = chat;
        this.context = context;
        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        if(chat.get(position).getSender_ID().equals(current_user_id))
        {
            holder.senderTextView.setText(chat.get(position).getMessage());
            holder.sender_datetime.setText(chat.get(position).getDatetime());
        }
        else
        {
            holder.receiver_TextView.setText(chat.get(position).getMessage());
            holder.receiver_datetime.setText(chat.get(position).getDatetime());
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

        if (message.getSender_ID().equals(current_user_id))
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVED;

    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView receiver_TextView, senderTextView, receiver_datetime, sender_datetime;

        public ViewHolder(View itemView)
        {
            super(itemView);
            receiver_TextView = itemView.findViewById(R.id.receiver_textView);
            senderTextView = itemView.findViewById(R.id.sender_textView);
            receiver_datetime = itemView.findViewById(R.id.receive_datetime);
            sender_datetime = itemView.findViewById(R.id.sender_datetime);
        }
    }
}
