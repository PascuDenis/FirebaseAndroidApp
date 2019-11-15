package com.example.db.activities.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.db.R;
import com.example.db.entity.Conversation;
import com.example.db.entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatFragment extends Fragment {
    private TextView textView;

    private Toolbar toolbar;
    private Spinner experianceSpinner;
    private EditText searchPeopleEditText;
    private GridLayout gridLayout;

    private Bundle bundle;

    private DatabaseReference mFirebaseDatabaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_messaging, container, false);

        textView = root.findViewById(R.id.textview);

        gridLayout = getActivity().findViewById(R.id.app_bar_gridview);
        experianceSpinner = getActivity().findViewById(R.id.spinnerSort);
        searchPeopleEditText = getActivity().findViewById(R.id.search_people_textview);


        ViewGroup.LayoutParams layoutParams = gridLayout.getLayoutParams();
        gridLayout.setLayoutParams(layoutParams);

        searchPeopleEditText.setText("");
        experianceSpinner.setEnabled(false);
        experianceSpinner.setAlpha(0);

        bundle = this.getArguments();
        if (bundle != null) {
            searchConversation(bundle.getString("CurrentUserId"), bundle.getString("GuestUserId"));
            textView.setText(bundle.getString("CurrentUserId") + "    " + bundle.getString("GuestUserId"));
        } else
            System.out.println("PROOOOOST");


        return root;
    }

    private void searchConversation(String currentUserId, String guestUserId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> conversationId = new ArrayList<>();
                conversationId.add(currentUserId);
                conversationId.add(guestUserId);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue().equals("conversations")) {
                        if (snapshot.getValue().equals("conversations")) {
                            databaseReference.child("conversations").child(conversationId.toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    } else {
                        if (conversationId.size() < 2) {
                            Toast.makeText(getContext(), "Conersation failed!\n Size < 2", Toast.LENGTH_LONG).show();
                            break;
                        }
                        Conversation conversation = new Conversation(conversationId);

                        databaseReference.child("conversation").setValue(conversation)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Conersation created!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Conersation failed!\n  " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
