package com.example.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView backButton;

    private TextInputEditText logInEmail,logInPassword;

    private TextView forgotPassword;
    private Button loginButton;

    private ProgressDialog loader;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){//if user is logged in
                    Intent intent = new Intent(LoginActivity.this, MainPage.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        backButton = findViewById(R.id.backButton);
        logInEmail = findViewById(R.id.loginEmail);
        logInPassword = findViewById(R.id.loginPassword);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginButton = findViewById(R.id.logInButton);


        loader = new ProgressDialog(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(LoginActivity.this, SelectRegistrationActivity.class);
                startActivity(intent); 
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = logInEmail.getText().toString().trim();
                final String password = logInPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    logInEmail.setError("Email is Required");
                }
                if(TextUtils.isEmpty(password)){
                    logInPassword.setError("Password is Required");
                }
                else{
                    loader.setMessage("Logging In");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Log In successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainPage.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                Log.i("ERROR",task.getException().toString());
                            }

                            loader.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}