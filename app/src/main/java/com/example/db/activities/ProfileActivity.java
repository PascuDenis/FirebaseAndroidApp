package com.example.db.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.db.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private TextView userEmail;
    private TextView userDetails;
    private Button userLogOutButton;
    private Button updateButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabase;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        userEmail = findViewById(R.id.userEmailTextView);
        userDetails = findViewById(R.id.userDetailsTextView);
        userLogOutButton = findViewById(R.id.logoutButton);
        updateButton = findViewById(R.id.updateButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        userEmail.setText(Objects.requireNonNull(firebaseUser).getEmail());
        userDetails.setText(userDatabase.getKey());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid() + "____________________________________________");
                startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class));
            }
        });

        userLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }
}
