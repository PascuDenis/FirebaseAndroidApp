package com.example.db.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.db.R;
import com.example.db.activities.MessageActivity;
import com.example.db.config.UploadImage;
import com.example.db.entity.User;
import com.example.db.recyclerview.UserProfileListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFollowerAdapter extends RecyclerView.Adapter<UserFollowerAdapter.ViewHolder> {
    private List<UserProfileListItem> followerProfileListItems;
    private Context context;
    private boolean isDark;
    private boolean isChat;

    public UserFollowerAdapter(Context context, List<UserProfileListItem> userProfileListItems, boolean isDark, boolean isChat) {
        this.context = context;
        this.followerProfileListItems = userProfileListItems;
        this.isDark = isDark;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_follower_recycler_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfileListItem listItem = followerProfileListItems.get(position);

        FirebaseDatabase.getInstance().getReference().child("uploads").child(listItem.getDisplayedUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getValue() + " - - ---");
                        if (dataSnapshot.getValue() != null) {
                            UploadImage object = dataSnapshot.getValue(UploadImage.class);
                            if (object.getDownloadUrl() != null) {
                                Picasso.get().load(object.getDownloadUrl()).into(holder.circleImageViewUserProfilePicture);
                            }
                            else {
                                Picasso.get().load(R.drawable.user_standard_profile_picture).into(holder.circleImageViewUserProfilePicture);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        holder.textViewUserUsername.setText(listItem.getDisplayedUsername());

        holder.cardView.setLongClickable(true);

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "LONG CLICK", Toast.LENGTH_SHORT).show();
                holder.showPopup(listItem.getDisplayedUserId());
                return true;
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("CurrentUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("GuestUserId", listItem.getDisplayedUserId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return followerProfileListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageViewUserProfilePicture;
        TextView textViewUserUsername;
        CircleImageView img_on;
        CircleImageView img_off;
        TextView last_msg;
        Dialog dialog;
        CardView cardView;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            cardView = itemView.findViewById(R.id.cartview);
            circleImageViewUserProfilePicture = itemView.findViewById(R.id.displayedUserProfilePicture);
            textViewUserUsername = itemView.findViewById(R.id.displayedUserUsername);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            dialog = new Dialog(context);

            if (isDark) {
                container.setBackgroundResource(R.drawable.card_dark_background);
                textViewUserUsername.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                container.setBackgroundResource(R.drawable.card_light_background);
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
            Button btnSendMessage;
            dialog.setContentView(R.layout.pop_up_user_follower);


            popupProfileImage = dialog.findViewById(R.id.profile_popup_picture);
            popupFullName = dialog.findViewById(R.id.profile_popup_fullname);
            popupEmail = dialog.findViewById(R.id.profile_popup_email);
            popupEducation = dialog.findViewById(R.id.profile_popup_education);
            popupNrFolowers = dialog.findViewById(R.id.popup_nr_of_followers);
            popupCountry = dialog.findViewById(R.id.pofile_popup_user_country);
            popupCity = dialog.findViewById(R.id.profile_popup_user_city);
            popupNrReputation = dialog.findViewById(R.id.popup_reputation_number);

            txtclose = dialog.findViewById(R.id.textviewclose);
            btnFollow = dialog.findViewById(R.id.btnLike);
            btnSendMessage = dialog.findViewById(R.id.btnsendmessage);

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

            hasReputation(FirebaseAuth.getInstance().getCurrentUser().getUid(), userId);

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addReputation(FirebaseAuth.getInstance().getCurrentUser().getUid(), userId);
                    btnFollow.setEnabled(false);
                    btnFollow.setText("ALREADY LIKED");
                }
            });

            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("CurrentUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    intent.putExtra("GuestUserId", userId);
                    context.startActivity(intent);

                    dialog.dismiss();

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

        private void hasReputation(String currentUserId, String followerUserId) {
            Button btnLike = dialog.findViewById(R.id.btnLike);
            FirebaseDatabase.getInstance().getReference().child("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User object = snapshot.getValue(User.class);
                                if (object.getId().equals(currentUserId)) {
                                    if (object.getReputationList() != null && object.getReputationList().contains(followerUserId)) {
                                        btnLike.setEnabled(false);
                                        btnLike.setText("ALREADY LIKED");
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
    private void addReputation(String currentUserId, String follwerUserId) {
        System.out.println(currentUserId + "    - - - - - - -  --  " + follwerUserId);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> reputationList;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User object = snapshot.getValue(User.class);
                            if (object.getId().equals(follwerUserId)) {
                                Integer reputationNumber = Integer.parseInt(String.valueOf(object.getNrOfFollowers()));
                                reputationNumber++;
                                databaseRef.child(follwerUserId).child("reputationNumber").setValue(reputationNumber);
                            }
                            if (object.getId().equals(currentUserId)) {
                                if (object.getReputationList() == null) {
                                    reputationList = new ArrayList<>();
                                } else {
                                    reputationList = object.getReputationList();
                                }
                                reputationList.add(follwerUserId);
                                databaseRef.child(currentUserId).child("reputationList").setValue(reputationList);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}
