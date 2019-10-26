package com.example.db.repository;

import androidx.annotation.NonNull;

import com.example.db.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUserRepository {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<User> users = new ArrayList<>();

    public interface DataStatus {
        void DataIsLoaded(List<User> users, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
    }

    public FirebaseUserRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
    }

    public void readUsers(final DataStatus dataStatus) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    User user = keyNode.getValue(User.class);
                    users.add(user);
                }
                dataStatus.DataIsLoaded(users, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addUser(User user, final DataStatus dataStatus) {
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(user).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dataStatus.DataIsInserted();
            }
        });
    }

    public void updateUser(String key, User user, final DataStatus dataStatus){
        System.out.println(key);
        databaseReference.child(key).setValue(user).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dataStatus.DataIsUpdated();
            }
        });
    }
}
