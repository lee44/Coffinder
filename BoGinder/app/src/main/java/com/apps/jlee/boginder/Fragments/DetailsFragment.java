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
import butterknife.BindView;
import butterknife.ButterKnife;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.apps.jlee.boginder.Activities.LoginRegisterActivity;
import com.apps.jlee.boginder.Activities.MainActivity;
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
import java.util.HashMap;
import java.util.Map;

public class DetailsFragment extends Fragment
{
    @BindView(R.id.name_et)
    EditText name_et;
    //@BindView(R.id.confirm_settings)
    //Button confirm_settings;
    @BindView(R.id.sign_out_settings)
    Button sign_out;
    @BindView(R.id.gender_radio_group)
    RadioGroup radioGroup;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String user_id, name, phone;
    private Context context;

    public DetailsFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        ButterKnife.bind(this,view);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users/"+user_id);

        getUserInfo();

        //confirm_settings.setOnClickListener(new View.OnClickListener()
        //{
        //    @Override
        //    public void onClick(View view)
        //    {
        //        saveUserInformation();
        //        Intent intent = new Intent(context, MainActivity.class);
        //        startActivity(intent);
        //    }
        //});

        sign_out.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                logoutUser(view);
            }
        });

        return view;
    }

    public void saveUserInformation()
    {
        name = name_et.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("Name", name);
        userInfo.put("Phone",phone);

        databaseReference.updateChildren(userInfo);
    }

    /**
     * Fetches user info from firebase database and binds it to the views
     */
    private void getUserInfo()
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0)
                {
                    Map<String, Object> map = (Map<String, Object>)dataSnapshot.getValue();

                    name_et.setText(map.get("Name") != null ? map.get("Name").toString() : "");
                    if(map.get("Gender") != null)
                        ((RadioButton)radioGroup.findViewById(map.get("Gender").toString().equals("Male") ? R.id.male_radio_button : R.id.female_radio_button)).setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    public void logoutUser(View view)
    {
        firebaseAuth.signOut();
        Intent intent = new Intent(context, LoginRegisterActivity.class);
        startActivity(intent);
    }
}
