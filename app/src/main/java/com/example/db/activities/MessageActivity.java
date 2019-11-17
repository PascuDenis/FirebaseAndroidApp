package com.example.db.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.db.R;
import com.example.db.activities.fragments.APIService;
import com.example.db.adapter.MessageAdapter;
import com.example.db.entity.Message;
import com.example.db.entity.User;
import com.example.db.entity.notification.Client;
import com.example.db.entity.notification.Data;
import com.example.db.entity.notification.MyResponse;
import com.example.db.entity.notification.Sender;
import com.example.db.entity.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private TextView username;
    private ImageButton sendButton;
    private EditText sendText;
    private ValueEventListener seenListener;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private Intent intent;
    private String followerUserId;

    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private RecyclerView recyclerView;

    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        sendButton = findViewById(R.id.btn_send);
        sendText = findViewById(R.id.text_send);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        followerUserId = intent.getStringExtra("GuestUserId");

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(followerUserId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getProfilePictureUrl() == null) {
                    profileImage.setImageResource(R.drawable.user_standard_profile_picture);
                } else {
                    Picasso.get().load(user.getProfilePictureUrl()).into(profileImage);
                }

                readMessage(firebaseUser.getUid(), followerUserId, user.getProfilePictureUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sendText.getText().toString();
                if (!message.equals("")) {
                    sentMessage(firebaseUser.getUid(), followerUserId, message);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send an empty message!", Toast.LENGTH_SHORT).show();
                }
                sendText.setText("");
            }
        });

        seenMessage(followerUserId);
    }


    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenListener);
        status("offline");
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void seenMessage(String userid) {
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message.getReceiver().equals(firebaseUser.getUid()) && message.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sentMessage(String sender, String reveiver, String message) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", reveiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
//        hashMap.put("timeCreated", timeCreated);

        FirebaseDatabase.getInstance().getReference("messages").push().setValue(hashMap);

        DatabaseReference conversationReferene = FirebaseDatabase.getInstance().getReference("conversations").child(firebaseUser.getUid())
                .child(followerUserId);

        conversationReferene.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    conversationReferene.child("id").setValue(followerUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                sendNotification(reveiver, user.getUsername(), msg);
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String reveiver, String username, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(reveiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.drawable.icon_app, username + ": " + msg, "New Message", followerUserId);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage(String myId, String userId, String imageUrl) {
        messageList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    System.out.println(message + " " + snapshot);
                    if (message.getReceiver().equals(userId) && message.getSender().equals(myId) ||
                            message.getReceiver().equals(myId) && message.getSender().equals(userId)) {
                        messageList.add(message);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, messageList, imageUrl, true);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void status(String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);

        databaseReference.updateChildren(map);

    }
}
