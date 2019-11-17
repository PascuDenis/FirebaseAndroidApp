package com.example.db.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.db.config.Config;
import com.example.db.config.GlideApp;
import com.example.db.config.UploadImage;
import com.example.db.activities.fragments.ProfileFragment;
import com.example.db.activities.fragments.SettingsFragment;
import com.example.db.activities.fragments.UpdateProfileFragment;
import com.example.db.activities.fragments.HomeFragment;

import com.example.db.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.db.entity.User;
import com.example.db.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private UserRepository repository;

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private CircleImageView imageViewUserProfilePicture;
    private View headerView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView reputationTextView;
    private TextView followersTextView;
    private final static int SELECT_PICTURE = 100;

    private Uri selectedImageUri;
    private boolean isDark;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private final static int imgWidth = 256;
    private final static int imgHeight = 256;
    private ArrayList<String> pathArray;
    private int arrayPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_navigation);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        repository = new UserRepository("users");

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        navigationView.setNavigationItemSelectedListener(this);

        headerView = getLayoutInflater().inflate(R.layout.nav_header_profile_navigation, drawer, false);
        navigationView.addHeaderView(headerView);

        imageViewUserProfilePicture = headerView.findViewById(R.id.user_profile_picture_imageButton);
        usernameTextView = headerView.findViewById(R.id.user_username_textView);
        emailTextView = headerView.findViewById(R.id.user_emailAddress_textView);
        reputationTextView = headerView.findViewById(R.id.user_reputation_number_textView);
        followersTextView = headerView.findViewById(R.id.user_number_of_followers_textView);

        getUserDetails();
        emailTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        isDark = Config.getThemeStatePref(getApplicationContext());

        if (isDark){
            navigationView.setBackgroundColor(getResources().getColor(R.color.ascend));
        } else {
            navigationView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }


        Uri imageUrl = firebaseUser.getPhotoUrl();
        System.out.println(imageUrl + "222222222222222222222222222");
        if (imageUrl == null) {
            getUserProfilePicture();
        }

        GlideApp.with(getApplicationContext()).load(imageUrl).into(imageViewUserProfilePicture);

        imageViewUserProfilePicture.setClickable(true);
        imageViewUserProfilePicture.bringToFront();
        imageViewUserProfilePicture.setOnClickListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_update,
                R.id.nav_myprofile,
                R.id.nav_chats,
                R.id.nav_followers,
                R.id.nav_settings,
                R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        handlePermission();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_profile_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
       closeApp("Are you sure you want to log out?");
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_update:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UpdateProfileFragment()).commit();
                break;
            case R.id.nav_myprofile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadImageFromStorage() {
        try {
            String path = pathArray.get(arrayPosition);
            File f = new File(path, "");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageViewUserProfilePicture.setImageBitmap(b);
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
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null && data.getData() != null) {
            // Get the url from data
            selectedImageUri = data.getData();
            Picasso.get().load(selectedImageUri).into(imageViewUserProfilePicture);

            //Uploading a image to firebase server
            if (selectedImageUri != null) {
                StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImageUri));

                fileReference.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(ProfileNavigationActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                                String imageName = System.currentTimeMillis() + "." + getFileExtension(selectedImageUri);

                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        UploadImage uploadImage = new UploadImage(
                                                imageName,
                                                taskSnapshot.getUploadSessionUri().toString(),
                                                uri.toString()
                                        );
                                        String uploadId = databaseReference.push().getKey();
//                                        databaseReference.child(userImageId).setValue(uploadImage);
                                    }
                                });


//                                databaseReference.child(uploadId).setValue(uploadImage);
                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(selectedImageUri)
                                        .build();

                                firebaseUser.updateProfile(profileUpdate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProfileNavigationActivity.this, "Profile picture updated!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            User object = snapshot.getValue(User.class);
                                            if (object.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                                repository.updateUserProfilePicture(new User(object.getId(), selectedImageUri.toString()));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileNavigationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                // add progress bar
                            }
                        });
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


//    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null && data.getData() != null) {
//                        // Get the url from data
//                       selectedImageUri = data.getData();
//                        if (null != selectedImageUri) {
//                            // Get the path from the Uri
//                            String path = getPathFromURI(selectedImageUri);
//                            // Set the image in ImageView
//                            imageViewUserProfilePicture.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    InputStream imageStream = null;
//                                    try {
//                                        imageStream = getContentResolver().openInputStream(
//                                                selectedImageUri);
//                                    } catch (FileNotFoundException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);
//
//                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                    bmp.compress(Bitmap.CompressFormat.PNG, SELECT_PICTURE, stream);
//                                    byte[] byteArray = stream.toByteArray();
//                                    try {
//                                        stream.close();
//                                        stream = null;
//                                    } catch (IOException e) {
//
//                                        e.printStackTrace();
//                                    }
//
//                                    imageViewUserProfilePicture.setImageURI(selectedImageUri);
//
//                                }
//                            });
//
//                        }
//                    }
//                }
//        }).start();
//    }

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
                        openAppSettings(ProfileNavigationActivity.this);
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

    @Override
    public void onClick(View v) {

        openImageChooser();
    }

    private void getUserDetails() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User object = snapshot.getValue(User.class);
                            if (object.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                usernameTextView.setText(object.getUsername());
                                reputationTextView.setText(String.valueOf(object.getReputationNumber()));
                                followersTextView.setText(String.valueOf(object.getNrOfFollowers()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getUserProfilePicture() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User object = snapshot.getValue(User.class);
                            if (object.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                if (object.getProfilePictureUrl() == null || object.getProfilePictureUrl().equals("")) {
                                    Glide.with(getApplicationContext()).load(object.getProfilePictureUrl()).into(imageViewUserProfilePicture);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Could not load profile picture!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileNavigationActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void closeApp(String message){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        logout();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


}
