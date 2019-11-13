package com.example.db.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.db.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private ProgressBar progressBar;
    private ImageView imageViewLogo;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignUp;
    private Button buttonLogin;
    private Button buttonForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBarSignUp);
        imageViewLogo = findViewById(R.id.imageViewLogo);
        editTextEmail = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonLogin = findViewById(R.id.buttonLogIn);
        buttonForgotPassword = findViewById(R.id.buttonForgottPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
//                                    if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
//                                        if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
//                                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                    } else {
//                                        Toast.makeText(LoginActivity.this, "Please verify your email address!", Toast.LENGTH_LONG).show();
//                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));

            }
        });
    }

}
