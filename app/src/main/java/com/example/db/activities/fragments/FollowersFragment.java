package com.example.db.activities.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.db.R;
import com.example.db.entity.User;
import com.example.db.adapter.UserFollowerAdapter;
import com.example.db.recyclerview.UserProfileListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.db.config.Config.getThemeStatePref;

public class FollowersFragment extends Fragment {

    private Spinner experianceSpinner;
    private TextView searchPeopleEditText;
    private Toolbar toolbar;
    private GridLayout gridLayout;

    private EditText search_followers;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    boolean isDark = false;
    private RelativeLayout rootLayout;

    private Button button;

    private List<UserProfileListItem> listItemList;
    private List<User> followersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_followers, container, false);

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

        search_followers = root.findViewById(R.id.search_followers);
        recyclerView = root.findViewById(R.id.followers_recyclerView);
        rootLayout = root.findViewById(R.id.followers_fragment_root_layout);

        listItemList = new ArrayList<>();
//        adapter = new UserFollowerAdapter(getContext(), listItemList, isDark, false);
//        recyclerView.setAdapter(adapter);

        button = root.findViewById(R.id.button);

        isDark = getThemeStatePref(getContext());
        if (isDark) {
            recyclerView.setBackgroundColor(getResources().getColor(R.color.hf_root_dark_mode));
            rootLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hf_root_dark_background));
        } else {
            recyclerView.setBackgroundColor(getResources().getColor(R.color.hf_root_light_mode));
            rootLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hf_root_light_background));
        }

        readFollowers();

        search_followers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFollowers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return root;
    }

    private void searchFollowers(String string) {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setAdapter(null);

        if (string.equals("")) {
            return;
        }

        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("username")
                .startAt(string)
                .endAt(string + "\uf8ff");

        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("followersList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listItemList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String followerId = String.valueOf(snapshot.getValue());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                        User follower = snapshot1.getValue(User.class);
                                        if (follower.getId().equals(followerId)) {
                                            System.out.println(follower);
                                            UserProfileListItem displayedUser = new UserProfileListItem(
                                                    firebaseUser.getUid(),
                                                    follower.getId(),
                                                    follower.getProfilePictureUrl(),
                                                    follower.getFullName(),
                                                    follower.getStatus());
                                            listItemList.add(displayedUser);
                                        }
                                    }
                                    adapter = new UserFollowerAdapter(getContext(), listItemList, isDark, false);
                                    recyclerView.setAdapter(adapter);
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

    private void readFollowers() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(null);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("followersList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> followersIdList = new ArrayList<>();
                        Set<String> followersSet = new HashSet<>();
                        listItemList = new ArrayList<>();

                        for (DataSnapshot followerIdSnapshot : dataSnapshot.getChildren()) {
                            String currentFollowerId = String.valueOf(followerIdSnapshot.getValue());
                            followersIdList.add(currentFollowerId);
                        }

                        for (String followerId : followersIdList) {
                            FirebaseDatabase.getInstance().getReference("users").orderByChild("username")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                User object = snapshot.getValue(User.class);
                                                if (object.getId().equals(followerId)) {
                                                    System.out.println(snapshot.getKey() + "--------------------");
                                                    UserProfileListItem displayedUser = new UserProfileListItem(
                                                            currentUserId,
                                                            object.getId(),
                                                            object.getProfilePictureUrl(),
                                                            object.getFullName(),
                                                            object.getStatus()
                                                    );
                                                    followersSet.add(displayedUser.getDisplayedUserId());
                                                }
                                            }

                                            List<UserProfileListItem> someList = new ArrayList<>();
                                            followersSet.forEach(x -> {
                                                FirebaseDatabase.getInstance().getReference().child("users")
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    User object = snapshot.getValue(User.class);
                                                                    if (object.getId().equals(x)) {
                                                                        System.out.println(snapshot.getKey() + "--------------------");
                                                                        UserProfileListItem displayedUser = new UserProfileListItem(
                                                                                currentUserId,
                                                                                object.getId(),
                                                                                object.getProfilePictureUrl(),
                                                                                object.getFullName(),
                                                                                object.getStatus()
                                                                        );
                                                                        someList.add(displayedUser);
                                                                    }
                                                                }
                                                                adapter = new UserFollowerAdapter(getContext(), someList, isDark, false);
                                                                System.out.println("2222222222222222222 FF_READ_adapter " + someList.size());
                                                                recyclerView.setAdapter(adapter);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            });
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
