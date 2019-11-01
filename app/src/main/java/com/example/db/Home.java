package com.example.db;

import android.app.Application;
import android.content.Intent;

import com.example.db.activities.ProfileActivity;
import com.example.db.activities.ProfileNavigationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

//        if (firebaseUser != null && firebaseUser.isEmailVerified()){
        if (firebaseUser != null){
//            startActivity(new Intent(Home.this, ProfileNavigationDrawerActivity.class));
            startActivity(new Intent(Home.this, ProfileNavigationActivity.class));
        }
    }
}
