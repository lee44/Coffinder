package com.apps.jlee.boginder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jlee.boginder.Activities.ProfilePreviewActivity;
import com.apps.jlee.boginder.Models.Match;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder>
{
    List<Match> matches;
    private Context context;

    public MatchesAdapter(List<Match> matches, Context context)
    {
        this.matches = matches;
        this.context = context;
    }

    @Override
    public MatchesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_match, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MatchesAdapter.ViewHolder holder, int position)
    {
        holder.matches_name.setText(matches.get(position).getName());
        if(!matches.get(position).getProfileImageUrl().equals("Default"))
            Glide.with(context).load(matches.get(position).getProfileImageUrl().get(0)).into(holder.matches_imageView);
        else
            holder.matches_imageView.setImageResource(R.mipmap.ic_launcher);

//        holder.matches_imageView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(view.getContext(), MatchProfileActivity.class);
//                intent.putExtra("user_id",matches.get(position).getUser_id());
//                view.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount()
    {
        return matches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView matches_imageView;
        TextView matches_name;

        public ViewHolder(View itemView)
        {
            super(itemView);

            matches_imageView = itemView.findViewById(R.id.item_matches_imageView);
            matches_name = itemView.findViewById(R.id.item_matches_name);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int i = getAdapterPosition();
                    Intent intent = new Intent(view.getContext(), ProfilePreviewActivity.class);
                    intent.putExtra("user_id",matches.get(i).getUser_id());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
