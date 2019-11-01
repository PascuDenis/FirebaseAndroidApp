package com.example.db.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.db.R;
import com.example.db.entity.ExperianceLevel;
import com.example.db.entity.Topic;
import com.example.db.entity.TopicNames;
import com.example.db.entity.User;
import com.example.db.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {
    List<Topic> topics;
    List<TopicNames> topicNames = new ArrayList<>();
    List<String> experianceLevels;
    private UserRepository repository;

    private TextView nameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        repository = new UserRepository("users");
        topics = new ArrayList<>();
        experianceLevels = new ArrayList<>(Arrays.asList(
                ExperianceLevel.BEGINNER.toString(),
                ExperianceLevel.NOVICE.toString(),
                ExperianceLevel.COMPETENT.toString(),
                ExperianceLevel.PROFICIENT.toString(),
                ExperianceLevel.EXPERT.toString()));


        init();
        setSpinnersData();
        handlePermission();

        imageViewProfilePicture.setClickable(true);
        imageViewProfilePicture.bringToFront();
        imageViewProfilePicture.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReferenceTopicNames = FirebaseDatabase.getInstance().getReference("topicNames");
        databaseReferenceExperianceLevels = FirebaseDatabase.getInstance().getReference("experianceLevels");

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

    }

    private void getUserDetails(){
        nameEditText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        emailEditText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
//        nameEditText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
//        nameEditText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

    }

    private void updateUser() {
        String name = nameEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User object = snapshot.getValue(User.class);
                            if (object.getEmail().equals(email)) {
                                repository.update(new User(object.getId(), name, username, email));
                                System.out.println(object.getId() + "----------------------------");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void addPathFiles() {
        String path = System.getenv("EXTERNAL_STORAGE");
        pathArray.add(path + "/Pictures/Portal/image1.jpg");
        loadImageFromStorage();
    }

    private void loadImageFromStorage() {
        try {
            String path = pathArray.get(arrayPosition);
            File f = new File(path, "");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageViewProfilePicture.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    private void handlePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    SELECT_PICTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SELECT_PICTURE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                    if (showRationale) {
                        //  Show your own message here
                    } else {
                        showSettingsAlert();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultCode == RESULT_OK) {
                    if (requestCode == SELECT_PICTURE) {
                        // Get the url from data
                        final Uri selectedImageUri = data.getData();
                        if (null != selectedImageUri) {
                            // Get the path from the Uri
                            String path = getPathFromURI(selectedImageUri);
                            // Set the image in ImageView
                            imageViewProfilePicture.post(new Runnable() {
                                @Override
                                public void run() {
                                    InputStream imageStream = null;
                                    try {
                                        imageStream = getContentResolver().openInputStream(
                                                selectedImageUri);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    try {
                                        stream.close();
                                        stream = null;
                                    } catch (IOException e) {

                                        e.printStackTrace();
                                    }

                                    imageViewProfilePicture.setImageURI(selectedImageUri);

                                }
                            });

                        }
                    }
                }
            }
        }).start();

    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        System.out.println(res + "??????????????????????????????");
        return res;
    }

    @Override
    public void onClick(View v) {
        openImageChooser();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openAppSettings(UpdateProfileActivity.this);
                    }
                });
        alertDialog.show();
    }

    public static void openAppSettings(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    private void setSpinnersData() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, experianceLevels);
        spinnerAlgorithms.setAdapter(adapter);
        spinnerDatabases.setAdapter(adapter);
        spinnerDataStructures.setAdapter(adapter);
        spinnerDesignPatterns.setAdapter(adapter);
        spinnerOop.setAdapter(adapter);
    }

    private void init() {
        nameEditText = findViewById(R.id.editTextFullName);
        usernameEditText = findViewById(R.id.editTextUsername);
        emailEditText = findViewById(R.id.editTextEmail);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        checkBoxAlgorithms = findViewById(R.id.checkBoxAlorithms);
        checkBoxDatabases = findViewById(R.id.checkBoxDatabases);
        checkBoxDataStructures = findViewById(R.id.checkBoxDatastructures);
        checkBoxDesignPatterns = findViewById(R.id.checkBoxDesignPatterns);
        checkBoxOop = findViewById(R.id.checkBoxOop);

        spinnerAlgorithms = findViewById(R.id.spinnerAlgorithms);
        spinnerDatabases = findViewById(R.id.spinnerDatabases);
        spinnerDataStructures = findViewById(R.id.spinnerDatastrutures);
        spinnerDesignPatterns = findViewById(R.id.spinnerDesignPatterns);
        spinnerOop = findViewById(R.id.spinnerOop);

        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
    }
}

/*      private DatabaseReference userDatabaseReference2;
        private DatabaseReference userDatabaseReference3;
        userDatabaseReference2 = FirebaseDatabase.getInstance().getReference("topicNames");
        userDatabaseReference3 = FirebaseDatabase.getInstance().getReference("experianceLevels");
        List<ExperianceLevel> experianceLevels = new ArrayList<>();
        experianceLevels.add(ExperianceLevel.BEGINNER);
        experianceLevels.add(ExperianceLevel.NOVICE);
        experianceLevels.add(ExperianceLevel.COMPETENT);
        experianceLevels.add(ExperianceLevel.PROFICIENT);
        experianceLevels.add(ExperianceLevel.EXPERT);
        userDatabaseReference2.setValue(experianceLevels);

        List<TopicNames> topicNames = new ArrayList<>();
        topicNames.add(TopicNames.ALGORITHMS);
        topicNames.add(TopicNames.DATABASES);
        topicNames.add(TopicNames.DATA_STRUCTURE);
        topicNames.add(TopicNames.DESIGN_PATTERNS);
        topicNames.add(TopicNames.OOP);
        userDatabaseReference3.setValue(topicNames);*/

