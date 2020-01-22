package com.apps.jlee.boginder.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.jlee.boginder.Models.Card;
import com.apps.jlee.boginder.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfilePreviewFragment extends Fragment
{
    @BindView(R.id.pic) ImageView image;
    @BindView(R.id.age_city) TextView age_city;
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
    @BindView(R.id.edit_floatingActionButton) FloatingActionButton editButton;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.no_data) TextView no_data;

    private Context context;
    private Card card;

    public ProfilePreviewFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile_preview, container, false);
        ButterKnife.bind(this, view);

        new CardAsyncTask().execute();

        editButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setFragment(new AccountFragment());
            }
        });

        return view;
    }

    private class CardAsyncTask extends AsyncTask<String, String, Card>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Card doInBackground(String... strings)
        {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String currentUser_id = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser_id);

            ValueEventListener valueEventListener = new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                     card = new Card(
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
                            dataSnapshot.child("ProfileImageUrl").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            };

            databaseReference.addValueEventListener(valueEventListener);

            return card;
        }

        @Override
        protected void onPostExecute(Card card)
        {
            super.onPostExecute(card);

            progressBar.setVisibility(View.GONE);
            Log.v("Lakers",card.toString());
            //loadUI(card);
        }
    }

    private void loadUI(Card card)
    {
        //if (card.getProfileImageUrl().equals("Default"))
        //{
        //    Glide.with(getContext()).load(R.mipmap.ic_launcher).into(image);
        //}
        //else
        //    Glide.with(getContext()).load(card.getProfileImageUrl()).into(image);

        name.setText(card.getName());
        age_city.setText(card.getAge() + ", " + card.getCity());
        height.setText(card.getHeight());
        job.setText(card.getJob().length() != 0 ? card.getJob() : "N/A");
        school.setText(card.getSchool().length() != 0 ? card.getSchool() : "N/A");
        description.setText(card.getDescription().length() != 0 ? card.getDescription() : "N/A");
        ethnicity.setText(card.getEthnicity().length() != 0 ? card.getEthnicity() : "N/A");
        religion.setText(card.getReligion().length() != 0 ? card.getReligion() : "N/A");
    }

    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}

