package com.example.db.activities.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.db.R;
import com.example.db.entity.ExperianceLevel;
import com.example.db.entity.Topic;
import com.example.db.entity.TopicNames;
import com.example.db.entity.User;
import com.example.db.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

    private ImageView imageViewProfilePicture;
    private final static int SELECT_PICTURE = 100;

    private final static int imgWidth = 256;
    private final static int imgHeight = 256;
    private ArrayList<String> pathArray;
    private int arrayPosition;
    private StorageReference storageReference;

    private DatabaseReference databaseReferenceTopicNames;
    private DatabaseReference databaseReferenceExperianceLevels;
    private FirebaseAuth firebaseAuth;

    private void init(View root) {
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

        imageViewProfilePicture = root.findViewById(R.id.imageViewProfilePicture);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_update_profile, container, false);

        init(root);


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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, experianceLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        setSpinnersData(adapter);

        getUserData();
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
        return root;
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
            topics.add(new Topic(TopicNames.DATABASES, ExperianceLevel.valueOf(spinnerAlgorithms.getSelectedItem().toString())));
        }
        if (checkBoxDataStructures.isChecked()) {
            topics.add(new Topic(TopicNames.DATA_STRUCTURE, ExperianceLevel.valueOf(spinnerAlgorithms.getSelectedItem().toString())));
        }
        if (checkBoxDesignPatterns.isChecked()) {
            topics.add(new Topic(TopicNames.DESIGN_PATTERNS, ExperianceLevel.valueOf(spinnerAlgorithms.getSelectedItem().toString())));
        }
        if (checkBoxOop.isChecked()) {
            topics.add(new Topic(TopicNames.OOP, ExperianceLevel.valueOf(spinnerAlgorithms.getSelectedItem().toString())));
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

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
//                .setDisplayName(name)
//                .setPhotoUri(Uri.parse("http://images.unsplash.com/photo-1529665253569-6d01c0eaf7b6?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&ixid=eyJhcHBfaWQiOjEyMDd9"))
//                .build();
//
//        user.updateProfile(profileUpdate)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
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
                                        if (topic.getTopicNames().equals(TopicNames.ALGORITHMS)) {
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