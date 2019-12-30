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
import com.apps.jlee.boginder.Models.Matches;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>
{
    private List<Matches> matches;
    private Context context;

    public MessagesAdapter(List<Matches> matches, Context context)
    {
        this.matches = matches;
        this.context = context;
    }

    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_last_message, parent, false);

        MessagesAdapter.ViewHolder viewHolder = new MessagesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position)
    {
        holder.message.setText(matches.get(position).getMessage());
        holder.name.setText(matches.get(position).getName());

        if(matches.get(position).getMessage_direction().equals("Sent"))
            holder.message_direction.setImageResource(R.drawable.arrow_left);
        else
            holder.message_direction.setImageResource(R.drawable.arrow_right);

        if(!matches.get(position).getProfileImageUrl().equals("Default"))
            Glide.with(context).load(matches.get(position).getProfileImageUrl()).into(holder.profile_image);
        else
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount()
    {
        return matches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView profile_image, message_direction;
        TextView name, message;

        public ViewHolder(View itemView)
        {
            super(itemView);

            profile_image = itemView.findViewById(R.id.item_matches_imageView);
            message_direction = itemView.findViewById(R.id.message_direction);
            name = itemView.findViewById(R.id.item_matches_name);
            message = itemView.findViewById(R.id.matches_message);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int i = getAdapterPosition();
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("MatchID",matches.get(i).getUser_id());
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}

