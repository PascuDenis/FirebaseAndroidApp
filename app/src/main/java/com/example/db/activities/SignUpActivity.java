package com.example.db.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.db.R;
import com.example.db.entity.Topic;
import com.example.db.entity.User;
import com.example.db.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private UserRepository repository;

    private ProgressBar progressBar;
    private EditText userFullName;
    private EditText userEmailAddress;
    private EditText userUsername;
    private EditText userPassword;
    private EditText userPasswordConfirm;
    private Button back;
    private Button userSignUp;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        repository = new UserRepository("users");

        progressBar = findViewById(R.id.progressBarSignUp);
        userFullName = findViewById(R.id.editTextFullName);
        userEmailAddress = findViewById(R.id.editTextUserEmailAddress);
        userUsername = findViewById(R.id.editTextUsername);
        userPassword = findViewById(R.id.editTextPassword);
        userPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        userSignUp = findViewById(R.id.userSignUp);
        back = findViewById(R.id.buttonBack);

        firebaseAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        userSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(userEmailAddress.getText().toString(), userPassword.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser fUser = firebaseAuth.getCurrentUser();
                                    addUser(fUser.getUid());
                                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            Toast.makeText(SignUpActivity.this, "Registred successfully!", Toast.LENGTH_LONG).show();

//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(SignUpActivity.this, "Registred successfully. Please check your email for verification!", Toast.LENGTH_LONG).show();
//                                                userEmailAddress.setText("");
//                                                userPassword.setText("");
//                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
//
//                                            } else {
//                                                Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                                            }

                                        }
                                    });
                                } else {
                                    Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    private void addUser(String authUserId) {
        String userId = authUserId;
        String fullName = userFullName.getText().toString();
        String email = userEmailAddress.getText().toString();
        String username = userUsername.getText().toString();
        String password = userPassword.getText().toString();
        String passwordConfirm = userPasswordConfirm.getText().toString();


        if (!TextUtils.isEmpty(email) || (!TextUtils.isEmpty(username))) {
            User user = new User(userId, fullName, username, email, "", "", "", "", 0, 0, new ArrayList<String >(), new ArrayList<Topic>(), "offline");

            repository.create(user);
//            userDatabase.child(id).setValue(user);
        } else {
            Toast.makeText(this, "You need to add an email or an username!", Toast.LENGTH_LONG).show();
        }

    }
}
