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
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.db.R;
import com.example.db.adapter.UserMessagesAdapter;
import com.example.db.entity.ConversationList;
import com.example.db.entity.User;
import com.example.db.entity.notification.Token;
import com.example.db.recyclerview.UserMessageListItem;
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

import static com.example.db.config.Config.getThemeStatePref;

public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;

    private Toolbar toolbar;
    private Spinner experianceSpinner;
    private TextView searchPeopleEditText;
    private GridLayout gridLayout;

    private UserMessagesAdapter userMessagesAdapter;
    private List<UserMessageListItem> mUsers;

    private List<ConversationList> usersList;
    boolean isDark;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

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

        isDark = getThemeStatePref(getContext());
        if (isDark) {
            recyclerView.setBackgroundColor(getResources().getColor(R.color.hf_root_dark_mode));
        } else {
            recyclerView.setBackgroundColor(getResources().getColor(R.color.hf_root_light_mode));
        }

        reference = FirebaseDatabase.getInstance().getReference("conversations").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ConversationList conversationList = snapshot.getValue(ConversationList.class);
                    usersList.add(conversationList);
                }
                System.out.println(usersList.size());
                if (usersList.size() == 0) {
                    FirebaseDatabase.getInstance().getReference("conversations").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String conversationId = snapshot.getKey();
                                System.out.println(snapshot + "memememememememe");
                                FirebaseDatabase.getInstance().getReference("conversations").child(conversationId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                            ConversationList conversationList = snapshot1.getValue(ConversationList.class);
                                            if (conversationList.getId().equals(firebaseUser.getUid())) {
                                                ConversationList object = new ConversationList(conversationId);
                                                usersList.add(object);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                conversationList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().

                getToken());

        return root;
    }

    private void updateToken(String token) {
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
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (ConversationList conversationList : usersList) {
                        if (user.getId().equals(conversationList.getId())) {
                            UserMessageListItem userMessageListItem = new UserMessageListItem(
                                    firebaseUser.getUid(),
                                    user.getId(),
                                    user.getProfilePictureUrl(),
                                    user.getUsername(),
                                    user.getStatus()
                            );

                            mUsers.add(userMessageListItem);
                        }
                        System.out.println(conversationList.getId());
                    }
                    System.out.println(mUsers.size() + " ------- ------- ------- " + usersList.size());
                }


                userMessagesAdapter = new UserMessagesAdapter(getContext(), mUsers, isDark, true);
                recyclerView.setAdapter(userMessagesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}