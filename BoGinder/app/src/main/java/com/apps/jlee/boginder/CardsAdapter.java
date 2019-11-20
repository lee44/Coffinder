package com.apps.jlee.boginder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item,parent,false);
        }

        TextView name = convertView.findViewById(R.id.potential_match_name);
        ImageView image = convertView.findViewById(R.id.potential_match_pic);

        name.setText(card.getName());
        image.setImageResource(R.mipmap.ic_launcher);

        return convertView;
    }
}
