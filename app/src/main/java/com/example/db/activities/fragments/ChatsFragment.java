package com.example.db.activities.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;

import com.example.db.R;
import com.example.db.adapter.UserFollowerAdapter;
import com.example.db.entity.ConversationList;
import com.example.db.entity.User;
import com.example.db.entity.notification.Token;
import com.example.db.recyclerview.UserProfileListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;

    private Toolbar toolbar;
    private Spinner experianceSpinner;
    private EditText searchPeopleEditText;
    private GridLayout gridLayout;

    private UserFollowerAdapter userAdapter;
    private List<UserProfileListItem> mUsers;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<ConversationList> usersList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chats, container, false);

        toolbar = getActivity().findViewById(R.id.toolbar);
        gridLayout = getActivity().findViewById(R.id.app_bar_gridview);
        experianceSpinner = getActivity().findViewById(R.id.spinnerSort);
        searchPeopleEditText = getActivity().findViewById(R.id.search_people_textview);

        ViewGroup.LayoutParams layoutParams = gridLayout.getLayoutParams();
        layoutParams.height = 60;
        gridLayout.setLayoutParams(layoutParams);

        gridLayout.setAlpha(0);
        searchPeopleEditText.setEnabled(false);
        experianceSpinner.setEnabled(false);

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("conversations").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ConversationList conversationList = snapshot.getValue(ConversationList.class);
                    usersList.add(conversationList);
                }
                conversationList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        reference = FirebaseDatabase.getInstance().getReference("messages");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Message message = snapshot.getValue(Message.class);
//
//                    if (message.getSented().equals(firebaseUser.getUid())) {
//                        usersList.add(message.getReceiver());
//                    }
//                    if (message.getReceiver().equals(firebaseUser.getUid())) {
//                        usersList.add(message.getSented());
//                    }
//                }
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return root;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void conversationList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("lallalaalal");
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    System.out.println("ooooooooo");
                    for (ConversationList conversationList : usersList) {
                        if (user.getId().equals(conversationList.getId())) {
                            UserProfileListItem userProfileListItem = new UserProfileListItem();
                            userProfileListItem.setCurrentUserId(firebaseUser.getUid());
                            userProfileListItem.setDisplayedUserId(user.getId());
                            userProfileListItem.setDisplayedProfilePicture(user.getProfilePictureUrl());
                            userProfileListItem.setDisplayedUsername(user.getUsername());
                            userProfileListItem.setDisplayedTopicName("");
                            userProfileListItem.setDisplayedExperianceLevel("");
                            userProfileListItem.setStatus(user.getStatus());
                            mUsers.add(userProfileListItem);
                        }
                        mUsers.forEach(x-> System.out.println(x + "-0-0-0-0-0-0-0-0-0-0"));
                    }
                    System.out.println(mUsers.size() + "  " + usersList.size());
                }
                userAdapter = new UserFollowerAdapter(getContext(), mUsers, true, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void readChats() {
//        mUsers = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference("users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mUsers.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    User user = snapshot.getValue(User.class);
//                    UserProfileListItem userProfileListItem = new UserProfileListItem();
//                    userProfileListItem.setCurrentUserId(firebaseUser.getUid());
//                    userProfileListItem.setDisplayedUserId(user.getId());
//                    userProfileListItem.setDisplayedProfilePicture(user.getProfilePictureUrl());
//                    userProfileListItem.setDisplayedUsername(user.getUsername());
//                    userProfileListItem.setDisplayedTopicName("");
//                    userProfileListItem.setDisplayedExperianceLevel("");
//                    userProfileListItem.setStatus(user.getStatus());
//
//                    for (String id : usersList) {
//                        if (user.getId().equals(id)) {
//                            if (mUsers.size() != 0) {
////                                for (ListIterator<UserProfileListItem> iterator = mUsers.listIterator(); iterator.hasNext(); ) {
////                                    UserProfileListItem user1 = iterator.next();
////                                    if (!user.getId().equals(user1.getDisplayedUserId())) {
////                                        mUsers.add(userProfileListItem);
////                                    }
////                                }
////                                for (UserProfileListItem user1 : mUsers){
////                                        mUsers.add(userProfileListItem);
////                                    }
//                            } else {
//                                mUsers.add(userProfileListItem);
//                            }
//                        }
//                    }
//                }
//                userAdapter = new UserFollowerAdapter(getContext(), mUsers, true, true);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}