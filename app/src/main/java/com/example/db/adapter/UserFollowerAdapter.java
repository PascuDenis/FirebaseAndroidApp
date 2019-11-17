package com.example.db.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.db.R;
import com.example.db.activities.MessageActivity;
import com.example.db.config.UploadImage;
import com.example.db.entity.Message;
import com.example.db.entity.User;
import com.example.db.recyclerview.UserProfileListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFollowerAdapter extends RecyclerView.Adapter<UserFollowerAdapter.ViewHolder> {
    private List<UserProfileListItem> followerProfileListItems;
    private Context context;
    private FirebaseStorage storageReference;
    private boolean isDark = false;
    private boolean isChat;
    private String theLastMessage;

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
        return new UserFollowerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfileListItem listItem = followerProfileListItems.get(position);
        storageReference = FirebaseStorage.getInstance();

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
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        holder.textViewUserUsername.setText(listItem.getDisplayedUsername());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("CurrentUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("GuestUserId", listItem.getDisplayedUserId());
                context.startActivity(intent);
            }
        });

        if (isChat) {
            lastMessage(listItem.getDisplayedUserId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (isChat) {
            if (listItem.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return followerProfileListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageViewUserProfilePicture;
        TextView textViewUserUsername;
        ImageView img_on;
        ImageView img_off;
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
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (firebaseUser != null && message != null) {
                        if (message.getReceiver().equals(firebaseUser.getUid()) && message.getSender().equals(userid) ||
                                message.getReceiver().equals(userid) && message.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = message.getMessage();
                        }
                    }

                }
                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
