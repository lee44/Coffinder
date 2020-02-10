package com.apps.jlee.boginder.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jlee.boginder.DialogFragments.PhotoDialogFragment;
import com.apps.jlee.boginder.Interfaces.ItemMoveCallback;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract, PhotoDialogFragment.DeletePhotoCallback
{
    private PhotoDialogFragment photoDialogFragment;
    private FragmentManager fragmentManager;
    private ArrayList<String> photos;
    private Context context;

    public PhotoAdapter(Context context, ArrayList<String> photos, FragmentManager fragmentManager)
    {
        this.context = context;
        this.photos = photos;
        this.fragmentManager = fragmentManager;
        photoDialogFragment = new PhotoDialogFragment(this);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photos, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        if(photos.get(position).equals("Default"))
        {
            holder.imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.add_picture_icon));
            holder.imageView.setTag("Empty");
        }
        else
        {
            Glide.with(context).load(photos.get(position)).into(holder.imageView);
            holder.imageView.setTag("Filled");
        }

        holder.imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("Status","Empty");
                bundle.putInt("Position", position);

                photoDialogFragment.setArguments(bundle);
                photoDialogFragment.show(fragmentManager,"PhotoDialogFragment");
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return photos.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition)
    {
        if(!photos.get(fromPosition).equals("Default") && !photos.get(toPosition).equals("Default"))
        {
            if (fromPosition < toPosition)
            {
                for (int i = fromPosition; i < toPosition; i++)
                {
                    Collections.swap(photos, i, i + 1);
                }
            }
            else
            {
                for (int i = fromPosition; i > toPosition; i--)
                {
                    Collections.swap(photos, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder)
    {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder)
    {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void deletePhoto(int position)
    {
        photos.set(position,"Default");
        notifyItemChanged(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        View rowView;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            rowView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
