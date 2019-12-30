package com.apps.jlee.boginder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.jlee.boginder.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChoicesAdapter extends RecyclerView.Adapter<ChoicesAdapter.ViewHolder>
{
    List<String> choices;
    private Context context;

    public ChoicesAdapter(List<String> choices, Context context)
    {
        this.choices = choices;
        this.context = context;
    }

    @Override
    public ChoicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_choices, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChoicesAdapter.ViewHolder holder, int position)
    {
        holder.choices.setText(choices.get(position));
    }

    @Override
    public int getItemCount()
    {
        return choices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView choices;

        public ViewHolder(View itemView)
        {
            super(itemView);

            choices = itemView.findViewById(R.id.choices);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });
        }
    }
}

