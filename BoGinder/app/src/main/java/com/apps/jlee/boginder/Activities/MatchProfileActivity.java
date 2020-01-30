package com.apps.jlee.boginder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.jlee.boginder.Interfaces.ProfileInterface;
import com.apps.jlee.boginder.Models.Card;
import com.apps.jlee.boginder.R;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MatchProfileActivity extends AppCompatActivity implements ProfileInterface.ProfileCallback
{
    @BindView(R.id.pic)
    ImageView image;
    @BindView(R.id.age_city)
    TextView age_city;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.height_header) TextView height_header;
    @BindView(R.id.height) TextView height;
    @BindView(R.id.imageView1) ImageView job_image;
    @BindView(R.id.job) TextView job;
    @BindView(R.id.imageView2) ImageView school_image;
    @BindView(R.id.school) TextView school;
    @BindView(R.id.description_header) TextView description_header;
    @BindView(R.id.description) TextView description;
    @BindView(R.id.religion_header) TextView religion_header;
    @BindView(R.id.religion) TextView religion;
    @BindView(R.id.ethnicity_header) TextView ethnicity_header;
    @BindView(R.id.ethnicity) TextView ethnicity;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.no_data) TextView no_data;
    @BindView(R.id.bottom_nav_bar)
    BottomNavigationView bottomNavigationView;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_profile);

        ButterKnife.bind(this);

        toggleUI(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String user_id = getIntent().getExtras().getString("user_id");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ArrayList<String> profileImageUrlArray = new ArrayList<>();
                long childrenSize = dataSnapshot.child("ProfileImageUrl").getChildrenCount();
                if(childrenSize != 0)
                {
                    for (DataSnapshot child : dataSnapshot.child("ProfileImageUrl").getChildren())
                    {
                        profileImageUrlArray.add(child.getValue().toString());
                    }
                }
                else
                {
                    profileImageUrlArray.add(dataSnapshot.child("ProfileImageUrl").getValue().toString());
                }

                Card card = new Card(
                        "",
                        dataSnapshot.child("Name").getValue().toString(),
                        dataSnapshot.child("Age").getValue().toString(),
                        dataSnapshot.child("Height").getValue().toString(),
                        dataSnapshot.child("City").getValue().toString(),
                        dataSnapshot.child("Occupation").exists() ? dataSnapshot.child("Occupation").getValue().toString() : "N/A",
                        dataSnapshot.child("School").exists() ? dataSnapshot.child("School").getValue().toString() : "N/A",
                        dataSnapshot.child("Ethnicity").exists() ? dataSnapshot.child("Ethnicity").getValue().toString() : "N/A",
                        dataSnapshot.child("Religion").exists() ? dataSnapshot.child("Religion").getValue().toString() : "N/A",
                        dataSnapshot.child("Description").exists() ? dataSnapshot.child("Description").getValue().toString() : "N/A",
                        profileImageUrlArray
                );

                loadUI(card);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                progressBar.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
            }
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                finish();
                return false;
            }
        });

        databaseReference.addValueEventListener(valueEventListener);
    }

    @Override
    public void loadUI(Card card)
    {
        if (card.getProfileImageUrl().get(0).equals("Default"))
        {
            Glide.with(this).load(R.mipmap.ic_launcher).into(image);
        }
        else
            Glide.with(this).load(card.getProfileImageUrl().get(0)).into(image);

        name.setText(card.getName());
        age_city.setText(card.getAge() + ", " + card.getCity());
        height.setText(card.getHeight());
        job.setText(card.getJob().length() != 0 ? card.getJob() : "N/A");
        school.setText(card.getSchool().length() != 0 ? card.getSchool() : "N/A");
        description.setText(card.getDescription().length() != 0 ? card.getDescription() : "N/A");
        ethnicity.setText(card.getEthnicity().length() != 0 ? card.getEthnicity() : "N/A");
        religion.setText(card.getReligion().length() != 0 ? card.getReligion() : "N/A");

        toggleUI(false);
    }

    public void toggleUI(boolean toggle)
    {
        if(toggle)
        {
            progressBar.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            age_city.setVisibility(View.GONE);
            job_image.setVisibility(View.GONE);
            job.setVisibility(View.GONE);
            school_image.setVisibility(View.GONE);
            school.setVisibility(View.GONE);
            height.setVisibility(View.GONE);
            height_header.setVisibility(View.GONE);
            description_header.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            religion_header.setVisibility(View.GONE);
            religion.setVisibility(View.GONE);
            ethnicity_header.setVisibility(View.GONE);
            ethnicity.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
            image.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            age_city.setVisibility(View.VISIBLE);
            job_image.setVisibility(View.VISIBLE);
            job.setVisibility(View.VISIBLE);
            school_image.setVisibility(View.VISIBLE);
            school.setVisibility(View.VISIBLE);
            height.setVisibility(View.VISIBLE);
            height_header.setVisibility(View.VISIBLE);
            description_header.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            religion_header.setVisibility(View.VISIBLE);
            religion.setVisibility(View.VISIBLE);
            ethnicity_header.setVisibility(View.VISIBLE);
            ethnicity.setVisibility(View.VISIBLE);
        }
    }
}
