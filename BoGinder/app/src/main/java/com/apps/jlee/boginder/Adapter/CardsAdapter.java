package com.apps.jlee.boginder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jlee.boginder.Models.Cards;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class CardsAdapter extends ArrayAdapter
{
    Context context;

    public CardsAdapter(Context context, int resourceID, List<Cards> items)
    {
        super(context,resourceID,items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Cards card = (Cards)getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card,parent,false);
        }

        TextView name = convertView.findViewById(R.id.potential_match_name);
        TextView age = convertView.findViewById(R.id.potential_match_age);
        TextView height = convertView.findViewById(R.id.potential_match_height);
        TextView distance = convertView.findViewById(R.id.potential_match_distance);
        ImageView image = convertView.findViewById(R.id.potential_match_pic);

        name.setText(card.getName());
        age.setText(card.getAge());
        height.setText(card.getHeight());
        distance.setText(card.getDistance());

        if(card.getProfileImageUrl().equals("Default"))
        {
            Glide.with(getContext()).load(R.mipmap.ic_launcher).into(image);
        }
        else
            Glide.with(getContext()).load(card.getProfileImageUrl()).into(image);

        return convertView;
    }
}
