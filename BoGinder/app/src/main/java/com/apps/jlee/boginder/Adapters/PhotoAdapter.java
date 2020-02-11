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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
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
    private int slot_position;

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
                slot_position = position;
                Bundle bundle = new Bundle();
                if(holder.imageView.getTag().equals("Empty"))
                {
                    bundle.putString("Status","Empty");
                }
                else
                {
                    bundle.putString("Status","Filled");
                }

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
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

            DatabaseReference filepath = databaseReference.child("ProfileImageUrl");

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
            for(int i = 0; i < 6; i++)
                filepath.child("Image"+i).setValue(photos.get(i));

            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder)
    {
        //myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder)
    {
        //myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void deletePhoto(int position)
    {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        DatabaseReference filepath = databaseReference.child("ProfileImageUrl");
        StorageReference imagePath = FirebaseStorage.getInstance().getReference().child("Profile_Image").child(user_id).child("Image"+position);

        photos.remove(position);
        photos.add("Default");

        for(int i = 0; i < 6; i++)
            filepath.child("Image"+i).setValue(photos.get(i));

        notifyDataSetChanged();

        imagePath.delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {

            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });
    }

    public int getSlot_position()
    {
        return slot_position;
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
