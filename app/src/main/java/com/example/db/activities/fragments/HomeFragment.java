package com.example.db.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.db.R;
import com.example.db.config.Config;
import com.example.db.entity.Topic;
import com.example.db.entity.User;
import com.example.db.recyclerview.UserProfileAdapter;
import com.example.db.recyclerview.UserProfileListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {
    private Spinner experianceSpinner;
    private EditText searchPeopleEditText;
    private Toolbar toolbar;
    private GridLayout gridLayout;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle bundleRecyclerViewState;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    boolean isDark = false;
    private CoordinatorLayout rootLayout;
    private Parcelable recyclerViewState;

    private List<UserProfileListItem> listItemList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = getActivity().findViewById(R.id.toolbar);
        gridLayout = getActivity().findViewById(R.id.app_bar_gridview);
        experianceSpinner = getActivity().findViewById(R.id.spinnerSort);
        searchPeopleEditText = getActivity().findViewById(R.id.search_people_textview);

        ViewGroup.LayoutParams layoutParams = gridLayout.getLayoutParams();
        layoutParams.height = 250;
        gridLayout.setLayoutParams(layoutParams);

        gridLayout.setAlpha(1);
        experianceSpinner.setEnabled(true);
        searchPeopleEditText.setEnabled(true);

        recyclerView = root.findViewById(R.id.user_recyclerView);
        rootLayout = root.findViewById(R.id.home_fragment_root_layout);

        isDark = Config.getThemeStatePref(getContext());
        if (isDark) {
//            rootLayout.setBackgroundColor(getResources().getColor(R.color.hf_root_dark_mode));
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_dark_mode));
            rootLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hf_root_dark_background));

        } else {
//            rootLayout.setBackgroundColor(getResources().getColor(R.color.hf_root_light_mode));
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_light_mode));
            rootLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.hf_root_light_background));
        }

        searchPeopleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listItemList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User object = snapshot.getValue(User.class);

                                String currentUserId;
                                if (object.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                    currentUserId = object.getId();
                                } else {
                                    currentUserId = "";
                                }

                                if (object.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    continue;
                                }

                                if (object.getTopicsList() != null) {
                                    for (Topic topic : object.getTopicsList()) {
                                        if (String.valueOf(topic.getTopicNames()).equals(searchPeopleEditText.getText().toString())) {

                                            UserProfileListItem displayedUser = new UserProfileListItem(
                                                    currentUserId,
                                                    object.getId(),
                                                    object.getProfilePictureUrl(),
                                                    object.getId(),
                                                    String.valueOf(topic.getTopicNames()),
                                                    String.valueOf(topic.getExperianceLevel())
                                            );
                                            listItemList.add(displayedUser);
                                            break;
                                        }
                                    }
                                }
                            }
//                            populateList();
                            sortByExperiance(listItemList);
                            adapter = new UserProfileAdapter(listItemList, getContext(), isDark);
                            System.out.println("11111111111111111111111111111111111111 adapter ");
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    return true;
                }
                return false;
            }
        });
        if (savedInstanceState != null) {

        }

        return root;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//
//        // save RecyclerView state
//
//        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
//        //save
//
////        bundleRecyclerViewState = new Bundle();
////        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
////        bundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (recyclerViewState != null){
//            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//        }
//        // restore RecyclerView state
////        if (bundleRecyclerViewState != null) {
////            new Handler().postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    Parcelable listState = bundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
////                    recyclerView.getLayoutManager().onRestoreInstanceState(listState);
////                }
////            }, 50);
////            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//////            Parcelable listState = bundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
//////            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
////        }
//    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


    }


    private void saveThemeStatePref(boolean isDark) {
        SharedPreferences preferences = getContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isDark", isDark);
        editor.commit();
    }

    private List<UserProfileListItem> sortByExperiance(List<UserProfileListItem> list) {
        String spinnerValue = experianceSpinner.getSelectedItem().toString();
//        if (!spinnerValue.equals(experianceSpinner.getItemAtPosition(0))) {
//            Collections.sort(list, new Comparator<UserProfileListItem>() {
//                @Override
//                public int compare(UserProfileListItem o1, UserProfileListItem o2) {
//                    return o1.getDisplayedExperianceLevel().compareTo(o2.getDisplayedExperianceLevel());
//                }
//            });
//
//            list.forEach(x -> System.out.println(x.getDisplayedUsername() + " " + x.getDisplayedExperianceLevel() + " ;;;;;;;;;;;;"));
//        }
//        return list;
        if (spinnerValue.equals(experianceSpinner.getItemAtPosition(1))) {
            Collections.sort(list, new Comparator<UserProfileListItem>() {
                @Override
                public int compare(UserProfileListItem o1, UserProfileListItem o2) {
                    return o1.getDisplayedExperianceLevel().compareTo(o2.getDisplayedExperianceLevel());
                }
            });

            list.forEach(x -> System.out.println(x.getDisplayedUsername() + " " + x.getDisplayedExperianceLevel() + " ;;;;;;;;;;;;"));
        } else if (spinnerValue.equals(experianceSpinner.getItemAtPosition(2))) {
            Collections.sort(list, new Comparator<UserProfileListItem>() {
                @Override
                public int compare(UserProfileListItem o1, UserProfileListItem o2) {
                    return o2.getDisplayedExperianceLevel().compareTo(o1.getDisplayedExperianceLevel());
                }
            });
        }
        return list;
    }

//
//    private void populateList() {
//        this.listItemList.add(new UserProfileListItem("Some ID 1", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 2", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 3", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 4", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 5", "content://com.android.providers.media.documents/document/image%3A16339", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 6", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 7", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 8", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 9", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 10", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 11", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 12", "content://com.android.providers.media.documents/document/image%3A16339", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 13", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 14", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 15", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 16", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 17", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 18", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 19", "content://com.android.providers.media.documents/document/image%3A16339", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 20", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 21", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 22", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 23", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 24", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 25", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 26", "content://com.android.providers.media.documents/document/image%3A16339", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 27", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 28", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//        this.listItemList.add(new UserProfileListItem("Some ID 29", "content://media/external/images/media/15292", "USERNAME1", "OOP", "BEGINNER"));
//    }

}