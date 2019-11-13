package com.example.db.activities.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.developer.kalert.KAlertDialog;
import com.example.db.R;
import com.example.db.activities.MainActivity;
import com.example.db.activities.ProfileNavigationActivity;
import com.example.db.config.Config;
import com.example.db.entity.ExperianceLevel;
import com.example.db.entity.Topic;
import com.example.db.entity.TopicNames;
import com.example.db.entity.User;
import com.example.db.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpdateProfileFragment extends Fragment {
    private UserRepository repository;
    List<String> experianceLevels;
    List<Topic> topics;

    private TextView nameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText cityEditText;
    private EditText countryEditText;
    private EditText educationEditText;

    private Button updateProfileButton;
    private CheckBox checkBoxAlgorithms;
    private CheckBox checkBoxDatabases;
    private CheckBox checkBoxDataStructures;
    private CheckBox checkBoxDesignPatterns;
    private CheckBox checkBoxOop;
    private Spinner spinnerAlgorithms;
    private Spinner spinnerDatabases;
    private Spinner spinnerDataStructures;
    private Spinner spinnerDesignPatterns;
    private Spinner spinnerOop;

    private Toolbar toolbar;
    private Spinner experianceSpinner;
    private EditText searchPeopleEditText;
    private GridLayout gridLayout;

    private boolean isDark;

    private final static int SELECT_PICTURE = 100;

    private final static int imgWidth = 256;
    private final static int imgHeight = 256;
    private ArrayList<String> pathArray;
    private int arrayPosition;

    private StorageReference storageReference;

    private DatabaseReference databaseReferenceTopicNames;
    private DatabaseReference databaseReferenceExperianceLevels;
    private FirebaseAuth firebaseAuth;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_update_profile, container, false);

        init(root);

        isDark = Config.getThemeStatePref(getContext());
        emailEditText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        repository = new UserRepository("users");
        topics = new ArrayList<>();
        experianceLevels = new ArrayList<>(Arrays.asList(
                ExperianceLevel.BEGINNER.toString(),
                ExperianceLevel.NOVICE.toString(),
                ExperianceLevel.COMPETENT.toString(),
                ExperianceLevel.PROFICIENT.toString(),
                ExperianceLevel.EXPERT.toString()));

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReferenceTopicNames = FirebaseDatabase.getInstance().getReference("topicNames");
        databaseReferenceExperianceLevels = FirebaseDatabase.getInstance().getReference("experianceLevels");

//        toolbar.setTitle("Update profile");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        if (isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_dark_mode));

        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_light_mode));
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, experianceLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        setSpinnersData(adapter);

        getUserData();
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();

                new KAlertDialog(getContext(), KAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Good job!")
                        .setContentText("Profile updated successfully!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                HomeFragment newHomeFragment = new HomeFragment();
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(((ViewGroup) Objects.requireNonNull(getView()).getParent()).getId(), newHomeFragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

                                ((MainActivity)getContext()).getNavigationView().getMenu().getItem(0).setChecked(true);
                            }
                        })
                        .show();
            }
        });
        return root;
    }

    private void init(View root) {
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


        nameEditText = root.findViewById(R.id.editTextFullName);
        usernameEditText = root.findViewById(R.id.editTextUsername);
        emailEditText = root.findViewById(R.id.editTextEmail);
        cityEditText = root.findViewById(R.id.editTextCity);
        countryEditText = root.findViewById(R.id.editTextCountry);
        educationEditText = root.findViewById(R.id.editTextEducation);


        updateProfileButton = root.findViewById(R.id.updateProfileButton);

        checkBoxAlgorithms = root.findViewById(R.id.checkBoxAlorithms);
        checkBoxDatabases = root.findViewById(R.id.checkBoxDatabases);
        checkBoxDataStructures = root.findViewById(R.id.checkBoxDatastructures);
        checkBoxDesignPatterns = root.findViewById(R.id.checkBoxDesignPatterns);
        checkBoxOop = root.findViewById(R.id.checkBoxOop);

        spinnerAlgorithms = root.findViewById(R.id.spinnerAlgorithms);
        spinnerDatabases = root.findViewById(R.id.spinnerDatabases);
        spinnerDataStructures = root.findViewById(R.id.spinnerDatastrutures);
        spinnerDesignPatterns = root.findViewById(R.id.spinnerDesignPatterns);
        spinnerOop = root.findViewById(R.id.spinnerOop);

        toolbar = getActivity().findViewById(R.id.toolbar);
    }

    private void updateUser() {
        String name = nameEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String country = countryEditText.getText().toString();
        String education = educationEditText.getText().toString();

        if (checkBoxAlgorithms.isChecked()) {
            topics.add(new Topic(TopicNames.ALGORITHMS, ExperianceLevel.valueOf(spinnerAlgorithms.getSelectedItem().toString())));
        }
        if (checkBoxDatabases.isChecked()) {
            topics.add(new Topic(TopicNames.DATABASES, ExperianceLevel.valueOf(spinnerDatabases.getSelectedItem().toString())));
        }
        if (checkBoxDataStructures.isChecked()) {
            topics.add(new Topic(TopicNames.DATA_STRUCTURE, ExperianceLevel.valueOf(spinnerDataStructures.getSelectedItem().toString())));
        }
        if (checkBoxDesignPatterns.isChecked()) {
            topics.add(new Topic(TopicNames.DESIGN_PATTERNS, ExperianceLevel.valueOf(spinnerDesignPatterns.getSelectedItem().toString())));
        }
        if (checkBoxOop.isChecked()) {
            topics.add(new Topic(TopicNames.OOP, ExperianceLevel.valueOf(spinnerOop.getSelectedItem().toString())));
        }

        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User object = snapshot.getValue(User.class);
                            if (object.getEmail().equals(email)) {
                                repository.update(new User(object.getId(), name, username, email, city, country, education, "", 0, 0, new ArrayList<>(), topics));
                                System.out.println(object.getId() + "----------------------------");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void getUserData() {
        String email = emailEditText.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User object = snapshot.getValue(User.class);
                            if (object.getEmail().equals(email)) {
                                nameEditText.setText(object.getFullName());
                                usernameEditText.setText(object.getUsername());
                                cityEditText.setText(object.getCityLocation());
                                countryEditText.setText(object.getCountryLocation());
                                educationEditText.setText(object.getEducation());

                                if (object.getTopicsList() != null) {
                                    object.getTopicsList().forEach(topic -> {
                                        if (topic.getTopicNames()!=null && topic.getTopicNames().equals(TopicNames.ALGORITHMS)) {
                                            checkBoxAlgorithms.setText("Algorithms");
                                            spinnerAlgorithms.setSelection(selectSpinnerText(topic.getExperianceLevel()));
                                        }
                                        if (topic.getTopicNames().equals(TopicNames.DATABASES)) {
                                            checkBoxDatabases.setText("Databases");
                                            spinnerDatabases.setSelection(selectSpinnerText(topic.getExperianceLevel()));
                                        }
                                        if (topic.getTopicNames().equals(TopicNames.DATA_STRUCTURE)) {
                                            checkBoxDataStructures.setText("Data structures");
                                            spinnerDataStructures.setSelection(selectSpinnerText(topic.getExperianceLevel()));
                                        }
                                        if (topic.getTopicNames().equals(TopicNames.DESIGN_PATTERNS)) {
                                            checkBoxDesignPatterns.setText("Design patterns");
                                            spinnerDesignPatterns.setSelection(selectSpinnerText(topic.getExperianceLevel()));
                                        }
                                        if (topic.getTopicNames().equals(TopicNames.OOP)) {
                                            checkBoxOop.setText("OOP");
                                            spinnerOop.setSelection(selectSpinnerText(topic.getExperianceLevel()));
                                        }

                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private int selectSpinnerText(ExperianceLevel experianceLevel) {
        switch (experianceLevel) {
            case BEGINNER:
                return 0;
            case NOVICE:
                return 1;
            case COMPETENT:
                return 2;
            case PROFICIENT:
                return 3;
            case EXPERT:
                return 4;
        }
        return -1;
    }

    private void setSpinnersData(ArrayAdapter<String> adapter) {

        spinnerAlgorithms.setAdapter(adapter);
        spinnerDatabases.setAdapter(adapter);
        spinnerDataStructures.setAdapter(adapter);
        spinnerDesignPatterns.setAdapter(adapter);
        spinnerOop.setAdapter(adapter);
    }
}