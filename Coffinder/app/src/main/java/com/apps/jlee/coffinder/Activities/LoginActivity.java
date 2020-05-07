package com.apps.jlee.coffinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apps.jlee.coffinder.Firebase.MyFirebaseMessagingService;
import com.apps.jlee.coffinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity
{
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.sign_in)
    Button sign_in;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference currentUserDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users");

        sign_in.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(email.getText().toString().trim().length() == 0)
                    email.setError("This field can not be blank");
                if(password.getText().toString().trim().length() == 0)
                    password.setError("This field can not be blank");
                if(email.getText().toString().trim().length() > 0 && password.getText().toString().trim().length() > 0)
                {
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Sign in error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        /*Instantiating interface will give compile time error i.e MyInterface foo = new MyInterface();
        * However instantiating a class that implements the interface is legal. Below we are instantiating an anonymous inner class*/
        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null)
                {
                    currentUserDB.child(user.getUid()).child("DeviceToken").setValue(MyFirebaseMessagingService.getToken(getBaseContext()));
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
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
        Intent intent = new Intent(LoginActivity.this, LoginRegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
