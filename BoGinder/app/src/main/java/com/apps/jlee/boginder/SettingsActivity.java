package com.apps.jlee.boginder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity
{
    @BindView(R.id.profile_image)
    ImageView profile_image;
    @BindView(R.id.name_et)
    EditText name_et;
    @BindView(R.id.phone_et)
    EditText phone_et;
    @BindView(R.id.confirm_settings)
    Button confirm_settings;
    @BindView(R.id.back_settings)
    Button back_settings;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String user_id, name, phone, profileImageURL;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        String gender = getIntent().getExtras().getString("Gender");
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users/"+gender+"/User_id/"+user_id);

        getUserInfo();

        profile_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        confirm_settings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveUserInformation();
            }
        });

        back_settings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    public void saveUserInformation()
    {
        name = name_et.getText().toString();
        phone = phone_et.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("Name", name);
        userInfo.put("Phone",phone);

        databaseReference.updateChildren(userInfo);

        //resultUri is the path to the image inside the device
        if(resultUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //Creates a tree with Profile_Image at the top followed by user_id
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile_Image").child(user_id);

            Bitmap bitmap = null;
            try
            {
                //Retrieves an image as a bitmap
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
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
                    Toast.makeText(SettingsActivity.this,"Image Upload Failed", Toast.LENGTH_SHORT).show();
                    finish();
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
                            Map userInfo = new HashMap();
                            userInfo.put("ProfileImageUrl",uri.toString());
                            databaseReference.updateChildren(userInfo);
                            finish();
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
                }
            });
        }
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
                    if(map.get("Name") != null)
                    {
                        name_et.setText(map.get("Name").toString());
                    }
                    if(map.get("Phone") != null)
                    {
                        phone_et.setText(map.get("Phone").toString());
                    }
                    if(map.get("ProfileImageUrl").equals("Default"))
                    {
                        Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(profile_image);
                    }
                    else
                        Glide.with(getApplication()).load(map.get("ProfileImageUrl").toString()).into(profile_image);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            /*URI is the location of the image in the phone*/
            resultUri = data.getData();
            profile_image.setImageURI(resultUri);
        }
    }
}
