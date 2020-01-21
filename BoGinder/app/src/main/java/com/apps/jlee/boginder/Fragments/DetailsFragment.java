package com.apps.jlee.boginder.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.jlee.boginder.Activities.LoginRegisterActivity;
import com.apps.jlee.boginder.Activities.MainActivity;
import com.apps.jlee.boginder.DialogFragment.ChoicesDialogFragment;
import com.apps.jlee.boginder.DialogFragment.HeightDialogFragment;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
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
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsFragment extends Fragment implements ChoicesDialogFragment.DialogFragmentClickListener, HeightDialogFragment.HeightDialogFragmentListener
{
    @BindView(R.id.name_et) EditText name_et;
    @BindView(R.id.email_et) EditText email_et;
    @BindView(R.id.age_et) EditText age_et;
    @BindView(R.id.height_et) EditText height_et;
    @BindView(R.id.ethnicity_et) EditText ethnicity_et;
    @BindView(R.id.religion_et) EditText religion_et;
    @BindView(R.id.city_et) EditText city_et;
    @BindView(R.id.occupation_et) EditText occupation_et;
    @BindView(R.id.school_et) EditText school_et;
    @BindView(R.id.gender_radio_group) RadioGroup gender_radio_group;
    @BindView(R.id.description_editText) EditText description_et;
    @BindView(R.id.sign_out_settings) Button sign_out;
    @BindView(R.id.scrollview) ScrollView scrollView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ChoicesDialogFragment choicesDialogFragment;
    private HeightDialogFragment heightDialogFragment;
    private String user_id;
    private Context context;
    private List<String> choices;

    public DetailsFragment(Context context)
    {
        this.context = context;
        choices = new ArrayList<>();
        heightDialogFragment = new HeightDialogFragment(this);
        choicesDialogFragment = new ChoicesDialogFragment(choices,this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Enables the toolbar menu
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

        height_et.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                heightDialogFragment.show(getFragmentManager(),"height_fragment");
            }
        });

        ethnicity_et.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                choices.clear();
                choices.add("Asian");choices.add("Arab");choices.add("African American");choices.add("Hispanic/Latino");
                choices.add("Native American");choices.add("Pacific Islander");choices.add("South Asian");choices.add("White");choices.add("Other");
                choicesDialogFragment.show(getFragmentManager(),"ethnicity");
            }
        });

        religion_et.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                choices.clear();
                choices.add("Buddhist");choices.add("Christian");choices.add("Catholic");choices.add("Hindu");choices.add("Jewish");choices.add("Muslim");choices.add("Sikh");
                choices.add("Shinto");choices.add("Spiritual but not religious");choices.add("Neither religious nor spiritual");choices.add("Other");
                choicesDialogFragment.show(getFragmentManager(),"religion");
            }
        });

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        saveUserInformation();
        return true;
    }

    public void saveUserInformation()
    {
        Map userInfo = new HashMap();
        userInfo.put("Name", name_et.getText().toString());
        userInfo.put("Email",email_et.getText().toString());
        userInfo.put("Age",age_et.getText().toString());
        userInfo.put("Height",height_et.getText().toString());
        userInfo.put("Ethnicity",ethnicity_et.getText().toString());
        userInfo.put("Religion",religion_et.getText().toString());
        userInfo.put("City",city_et.getText().toString());
        userInfo.put("Occupation",occupation_et.getText().toString());
        userInfo.put("School",school_et.getText().toString());

        int selectId = gender_radio_group.getCheckedRadioButtonId();
        if(selectId == R.id.male_radio_button)
            userInfo.put("Gender","Male");
        else
            userInfo.put("Gender","Female");

        userInfo.put("Description",description_et.getText().toString());

        //updateChildren will either update existing child or create the child
        databaseReference.updateChildren(userInfo).addOnSuccessListener(new OnSuccessListener()
        {
            @Override
            public void onSuccess(Object o)
            {
                Snackbar snackbar = Snackbar.make(scrollView, "Saved", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                textView.setTextSize(22);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                snackbar.show();
            }
        });
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
                    email_et.setText(map.get("Email") != null ? map.get("Email").toString() : "");
                    age_et.setText(map.get("Age") != null ? map.get("Age").toString() : "");
                    height_et.setText(map.get("Height") != null ? map.get("Height").toString() : "");
                    ethnicity_et.setText(map.get("Ethnicity") != null ? map.get("Ethnicity").toString() : "");
                    religion_et.setText(map.get("Religion") != null ? map.get("Religion").toString() : "");
                    city_et.setText(map.get("City") != null ? map.get("City").toString() : "");
                    occupation_et.setText(map.get("Occupation") != null ? map.get("Occupation").toString() : "");
                    school_et.setText(map.get("School") != null ? map.get("School").toString() : "");
                    if(map.get("Gender") != null)
                        ((RadioButton)gender_radio_group.findViewById(map.get("Gender").toString().equals("Male") ? R.id.male_radio_button : R.id.female_radio_button)).setChecked(true);
                    description_et.setText(map.get("Description") != null ? map.get("Description").toString() : "");
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

    @Override
    public void choiceDialogFragmentClicked(String tag, String choice)
    {
        if(tag.equals("ethnicity"))
            ethnicity_et.setText(choice);
        else if(tag.equals("religion"))
            religion_et.setText(choice);
    }

    @Override
    public void heightDialogFragmentClicked(String feet, String inches)
    {
        height_et.setText(feet+inches);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //Log.v("Lakers","onStart");

    }
    @Override
    public void onResume()
    {
        super.onResume();
        //Log.v("Lakers","onResume");

    }
    @Override
    public void onPause()
    {
        super.onPause();
        //Log.v("Lakers","onPause");

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Log.v("Lakers","onDestroy");
    }
}
