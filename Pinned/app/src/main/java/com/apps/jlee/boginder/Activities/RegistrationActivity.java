package com.apps.jlee.boginder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.apps.jlee.boginder.DialogFragments.HeightDialogFragment;
import com.apps.jlee.boginder.Firebase.MyFirebaseMessagingService;
import com.apps.jlee.boginder.Models.Users;
import com.apps.jlee.boginder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistrationActivity extends AppCompatActivity implements HeightDialogFragment.HeightDialogFragmentListener
{
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.age)
    EditText age;
    @BindView(R.id.height)
    EditText height;
    @BindView(R.id.gender_radio_group)
    RadioGroup gender_radioGroup;
    @BindView(R.id.orientation_radio_group)
    RadioGroup orientation_radioGroup;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.register)
    Button register;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private HeightDialogFragment heightDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        heightDialogFragment = new HeightDialogFragment(this);

        ((RadioButton)(gender_radioGroup.findViewById(R.id.male_radio_button))).setChecked(true);
        ((RadioButton)(orientation_radioGroup.findViewById(R.id.orientation_female_radio_button))).setChecked(true);

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(name.getText().toString().trim().length() == 0)
                    name.setError("This field can not be blank");
                else if(age.getText().toString().trim().length() == 0)
                    age.setError("This field can not be blank");
                else if(height.getText().toString().trim().length() == 0)
                    height.setError("This field can not be blank");
                else if(email.getText().toString().trim().length() == 0)
                    email.setError("This field can not be blank");
                else if(password.getText().toString().trim().length() == 0)
                    password.setError("This field can not be blank");
                else
                {
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(RegistrationActivity.this, "Sign up Error", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                int selectId = gender_radioGroup.getCheckedRadioButtonId();
                                RadioButton gender_radioButton = findViewById(selectId);
                                selectId = orientation_radioGroup.getCheckedRadioButtonId();
                                RadioButton orientation_radioButton = findViewById(selectId);

                                String userId = firebaseAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users");

                                currentUserDB.child(userId).setValue(new Users(name.getText().toString(),age.getText().toString(),height.getText().toString(),gender_radioButton.getText().toString(),orientation_radioButton.getText().toString(),"","Default", MyFirebaseMessagingService.getToken(getBaseContext()))).addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //
                //if(user != null)
                //{
                //    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                //    startActivity(intent);
                //    finish();
                //    return;
                //}
            }
        };

        height.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                heightDialogFragment.show(getSupportFragmentManager(),"height_fragment");
            }
        });
    }

    @Override
    public void heightDialogFragmentClicked(String feet, String inches)
    {
        height.setText(feet+inches);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (getCurrentFocus() != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(RegistrationActivity.this, LoginRegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
