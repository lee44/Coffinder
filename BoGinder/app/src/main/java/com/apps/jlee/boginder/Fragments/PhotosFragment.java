package com.apps.jlee.boginder.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.apps.jlee.boginder.Adapters.PhotoAdapter;
import com.apps.jlee.boginder.Interfaces.ItemMoveCallback;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotosFragment extends Fragment
{
    @BindView(R.id.photo_recycleview)
    RecyclerView photo_rv;

    private DatabaseReference databaseReference;
    private List<Uri> resultUri;
    ArrayList<String> imageUrlsList;
    private PhotoAdapter photoAdapter;
    private Context context;
    private String user_id;

    public PhotosFragment(Context context)
    {
        this.context = context;
        resultUri = new ArrayList<>();
        imageUrlsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        ButterKnife.bind(this,view);

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users/"+user_id+"/ProfileImageUrl");

        photoAdapter = new PhotoAdapter(getContext(),imageUrlsList);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(photoAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(photo_rv);
        photo_rv.setLayoutManager(new GridLayoutManager(context,3));
        photo_rv.setAdapter(photoAdapter);

        getUserPhotos();

//        imageView5.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent,5);
//            }
//        });

        return view;
    }

    public void saveUserInformation(Uri uri, final int imageSlot)
    {
        //resultUri is the path to the image inside the device
        if(uri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            //progressDialog.show();

            //Creates a tree with Profile_Image at the top followed by user_id
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile_Image").child(user_id).child("Image"+imageSlot);

            Bitmap bitmap = null;
            try
            {
                //Retrieves an image as a bitmap
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            }catch (IOException e){e.printStackTrace();}

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            //Since putBytes() accepts a byte[], it requires your app to hold the entire contents of a file in memory at once.
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(context,"Image Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            //uri has the url and store it in the firebase database tree
                            databaseReference.child("Image"+imageSlot).setValue(uri.toString());
                        }
                    });
                }
            });
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot)
                {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");

                    if(progress == 100)
                        progressDialog.dismiss();
                }
            });
        }
    }

    /**
     * Fetches user photos and binds it to the views
     */
    private void getUserPhotos()
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0)
                {
                    for(DataSnapshot child : dataSnapshot.getChildren())
                    {
                        imageUrlsList.add(child.getValue().toString());
                    }
                }
                initializeArrayList();
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(resultCode == Activity.RESULT_OK)
        {
            Uri uri = data.getData();
//            if(requestCode == 0)
//                imageView0.setImageURI(uri);
//            else if(requestCode == 1)
//                imageView1.setImageURI(uri);
//            else if(requestCode == 2)
//                imageView2.setImageURI(uri);
//            else if(requestCode == 3)
//                imageView3.setImageURI(uri);
//            else if(requestCode == 4)
//                imageView4.setImageURI(uri);
//            else if(requestCode == 5)
//                imageView5.setImageURI(uri);

            saveUserInformation(uri,requestCode);
        }
    }

    public void initializeArrayList()
    {
        for(int i = imageUrlsList.size(); i < 6; i++)
        {
            imageUrlsList.add("Default");
        }
    }
}
