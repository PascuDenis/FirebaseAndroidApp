package com.example.db.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.db.R;
import com.example.db.activities.fragments.HomeFragment;
import com.example.db.activities.fragments.ProfileFragment;
import com.example.db.activities.fragments.SettingsFragment;
import com.example.db.activities.fragments.UpdateProfileFragment;
import com.example.db.config.Config;
import com.example.db.config.GlideApp;
import com.example.db.config.UploadImage;
import com.example.db.entity.User;
import com.example.db.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private EditText toolbarSearchText;
    private Spinner toolbarSpinner;

    private UserRepository repository;

    private CircleImageView imageViewUserProfilePicture;
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

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new UserRepository("users");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        drawerToggle.syncState();
        drawerToggle.setDrawerIndicatorEnabled(true);
        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
// Inflate the header view at runtime
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_profile_navigation);
// We can now look up items within the header if needed
        imageViewUserProfilePicture = headerLayout.findViewById(R.id.user_profile_picture_imageButton);
        usernameTextView = headerLayout.findViewById(R.id.user_username_textView);
        emailTextView = headerLayout.findViewById(R.id.user_emailAddress_textView);
        reputationTextView = headerLayout.findViewById(R.id.user_reputation_number_textView);
        followersTextView = headerLayout.findViewById(R.id.user_number_of_followers_textView);

        toolbarSearchText = toolbar.findViewById(R.id.search_people_textview);
        toolbarSpinner = toolbar.findViewById(R.id.spinnerSort);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserDetails();
        emailTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        isDark = Config.getThemeStatePref(getApplicationContext());


        if (isDark) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.navigation_dark_mode));
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_dark_mode));
            toolbarSearchText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        } else {
            navigationView.setBackgroundColor(getResources().getColor(R.color.navigation_light_mode));
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_light_mode));
            toolbarSearchText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }


        Uri imageUrl = firebaseUser.getPhotoUrl();
        System.out.println(imageUrl + "222222222222222222222222222");
        if (imageUrl == null) {
            getUserProfilePicture();
        }
        Picasso.with(getApplicationContext()).load(imageUrl).into(imageViewUserProfilePicture);

        imageViewUserProfilePicture.setClickable(true);
        imageViewUserProfilePicture.bringToFront();
        imageViewUserProfilePicture.setOnClickListener(this);

        handlePermission();

        HomeFragment newHomeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flContent, newHomeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        getNavigationView().getMenu().getItem(0).setChecked(true);

    }

    @Override
    public void onClick(View v) {

        openImageChooser();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new HomeFragment()).commit();
                break;
            case R.id.nav_update:
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new UpdateProfileFragment()).commit();
                break;
            case R.id.nav_myprofile:
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new ProfileFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new SettingsFragment()).commit();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        closeApp("Are you sure you want to log out?");
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

    public NavigationView getNavigationView() {
        return this.nvDrawer;
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

    private void getUserDetails() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            System.out.println(snapshot.getValue(User.class) + "Ooooooooooooooooooooooooo");
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

    private void handlePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    SELECT_PICTURE);
        }
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
            Picasso.with(this).load(selectedImageUri).into(imageViewUserProfilePicture);
            String userImageId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //Uploading a image to firebase server
            if (selectedImageUri != null) {
                StorageReference fileReference = storageReference.child(userImageId);

                fileReference.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                                String imageName = userImageId;
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        System.out.println(uri.toString() + " 0 0 0 0 0 0 0 0 0 0 0 0  0 " + userImageId);
                                        UploadImage uploadImage = new UploadImage(
                                                imageName,
                                                taskSnapshot.getUploadSessionUri().toString(),
                                                uri.toString()
                                        );
                                        databaseReference.child(userImageId).setValue(uploadImage);
                                        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    User object = snapshot.getValue(User.class);
                                                    if (object.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                                        repository.updateUserProfilePicture(new User(object.getId(), uri.toString()));
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });

                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(selectedImageUri)
                                        .build();

                                firebaseUser.updateProfile(profileUpdate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "Profile picture updated!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        openAppSettings(MainActivity.this);
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

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        MainActivity.this.onNavigationItemSelected(menuItem);
                        return true;
                    }
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void closeApp(String message) {
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
