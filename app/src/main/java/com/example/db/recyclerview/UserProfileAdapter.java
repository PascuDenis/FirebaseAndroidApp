package com.example.db.recyclerview;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.db.R;
import com.example.db.config.GlideApp;
import com.example.db.config.UploadImage;
import com.example.db.entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ViewHolder> {
    private List<UserProfileListItem> userProfileListItems;
    private Context context;
    private FirebaseStorage storageReference;
    boolean isDark = false;

    public UserProfileAdapter(List<UserProfileListItem> userProfileListItems, Context context, boolean isDark) {
        this.userProfileListItems = userProfileListItems;
        this.context = context;
        this.isDark = isDark;
    }

    public UserProfileAdapter(List<UserProfileListItem> listItems, Context context) {
        this.userProfileListItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_recycler_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfileListItem listItem = userProfileListItems.get(position);
        storageReference = FirebaseStorage.getInstance();

        // Transition animation for user profile image and cardview
        holder.circleImageViewUserProfilePicture.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        FirebaseDatabase.getInstance().getReference().child("uploads").child(listItem.getDisplayedUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getValue() + " - - ---");
                        if (dataSnapshot.getValue() != null) {
                            UploadImage object = dataSnapshot.getValue(UploadImage.class);
                            if (object.getDownloadUrl() != null) {
                                Picasso.with(context).load(object.getDownloadUrl()).into(holder.circleImageViewUserProfilePicture);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


//        App.with(context)
//                .load(storageReference)
//                .into(holder.circleImageViewUserProfilePicture);

//        Picasso.with(context).load(listItem.getDisplayedProfilePicture()).into(holder.circleImageViewUserProfilePicture);
        holder.textViewUserUsername.setText(listItem.getDisplayedUsername());
        holder.textViewUserTopicName.setText(listItem.getDisplayedTopicName() + "  " + listItem.getDisplayedExperianceLevel());


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showPopup(listItem.getDisplayedUserId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return userProfileListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageViewUserProfilePicture;
        TextView textViewUserUsername;
        TextView textViewUserTopicName;
        Dialog dialog;
        CardView cardView;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            cardView = itemView.findViewById(R.id.cartview);
            circleImageViewUserProfilePicture = itemView.findViewById(R.id.displayedUserProfilePicture);
            textViewUserUsername = itemView.findViewById(R.id.displayedUserUsername);
            textViewUserTopicName = itemView.findViewById(R.id.displayedUserTopicName);
            dialog = new Dialog(context);

            if (isDark) {
                container.setBackgroundResource(R.drawable.card_dark_background);
                textViewUserTopicName.setTextColor(ContextCompat.getColor(context, R.color.white));
                textViewUserUsername.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                container.setBackgroundResource(R.drawable.card_light_background);
                textViewUserTopicName.setTextColor(ContextCompat.getColor(context, R.color.black));
                textViewUserUsername.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        }

        public void showPopup(String userId) {

            CircleImageView popupProfileImage;
            TextView popupFullName;
            TextView popupEmail;
            TextView popupEducation;
            TextView popupNrFolowers;
            TextView popupCountry;
            TextView popupCity;
            TextView popupNrReputation;

            TextView txtclose;
            Button btnFollow;
            Button btnsendmessage;
            dialog.setContentView(R.layout.pop_up_user_profile);


            popupProfileImage = dialog.findViewById(R.id.profile_popup_picture);
            popupFullName = dialog.findViewById(R.id.profile_popup_fullname);
            popupEmail = dialog.findViewById(R.id.profile_popup_email);
            popupEducation = dialog.findViewById(R.id.profile_popup_education);
            popupNrFolowers = dialog.findViewById(R.id.popup_nr_of_followers);
            popupCountry = dialog.findViewById(R.id.pofile_popup_user_country);
            popupCity = dialog.findViewById(R.id.profile_popup_user_city);
            popupNrReputation = dialog.findViewById(R.id.popup_reputation_number);

            txtclose = dialog.findViewById(R.id.textviewclose);
            btnFollow = dialog.findViewById(R.id.btnfollow);
            btnsendmessage = dialog.findViewById(R.id.btnsendmessage);


            popupProfileImage.setImageDrawable(circleImageViewUserProfilePicture.getDrawable());
            FirebaseDatabase.getInstance().getReference().child("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User object = snapshot.getValue(User.class);
                                if (object.getId().equals(userId)) {
                                    popupFullName.setText(object.getFullName());
                                    popupEmail.setText(object.getEmail());
                                    popupEducation.setText(object.getEducation());
//                                    popupNrFolowers.setText(object.getNrOfFollowers());
                                    popupCountry.setText(object.getCountryLocation());
                                    popupCity.setText(object.getCityLocation());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            hasFollower(FirebaseAuth.getInstance().getCurrentUser().getUid(), userId);

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFollower(FirebaseAuth.getInstance().getCurrentUser().getUid(), userId);
                    btnFollow.setEnabled(false);
                    btnFollow.setText("FOLLOWING");
                    btnFollow.setTextColor(Color.GREEN);
                }
            });

            btnsendmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

        private void hasFollower(String currentUserId, String followedUserId) {
            Button btnFollow = dialog.findViewById(R.id.btnfollow);
            FirebaseDatabase.getInstance().getReference().child("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User object = snapshot.getValue(User.class);
                                if (object.getId().equals(currentUserId)) {
                                    if (object.getFollowersList() != null && object.getFollowersList().contains(followedUserId)) {
                                        btnFollow.setEnabled(false);
                                        btnFollow.setText("FOLLOWING");
                                        btnFollow.setTextColor(Color.GREEN);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    private void addFollower(String currentUserId, String followerId) {
        boolean succeded = false;
        System.out.println(currentUserId + "    - - - - - - -  --  " + followerId);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> followersList;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User object = snapshot.getValue(User.class);
                            if (object.getId().equals(followerId)) {
                                Integer nrOfFollowers = Integer.parseInt(String.valueOf(object.getNrOfFollowers()));
                                nrOfFollowers++;
                                databaseRef.child(followerId).child("nrOfFollowers").setValue(nrOfFollowers);
                            }
                            if (object.getId().equals(currentUserId)) {
                                if (object.getFollowersList() == null) {
                                    followersList = new ArrayList<>();
                                } else {
                                    followersList = object.getFollowersList();
                                }
                                followersList.add(followerId);
                                databaseRef.child(currentUserId).child("followersList").setValue(followersList);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}
