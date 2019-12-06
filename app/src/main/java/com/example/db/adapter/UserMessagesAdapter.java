package com.example.db.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.db.R;
import com.example.db.activities.MessageActivity;
import com.example.db.config.UploadImage;
import com.example.db.entity.Message;
import com.example.db.recyclerview.UserMessageListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.db.config.Config.getThemeStatePref;

public class UserMessagesAdapter extends RecyclerView.Adapter<UserMessagesAdapter.ViewHolder> {
    private List<UserMessageListItem> userMessageListItems;
    private Context context;
    private boolean isDark;
    private boolean isChat;
    private String theLastMessage;

    public UserMessagesAdapter(Context context, List<UserMessageListItem> userMessageListItems, boolean isDark, boolean isChat) {
        this.context = context;
        this.userMessageListItems = userMessageListItems;
        this.isDark = isDark;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user_messages_recycler_view, parent, false);
        return new UserMessagesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserMessageListItem listItem = userMessageListItems.get(position);

        FirebaseDatabase.getInstance().getReference().child("uploads").child(listItem.getDisplayedUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getValue() + " - - ---");
                        if (dataSnapshot.getValue() != null) {
                            UploadImage object = dataSnapshot.getValue(UploadImage.class);
                            if (object.getDownloadUrl() != null) {
                                Picasso.get().load(object.getDownloadUrl()).into(holder.userProfilePicture);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        holder.userUsername.setText(listItem.getDisplayedUsername());

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
            lastMessage(listItem.getDisplayedUserId(), holder.userLastMessage);
        } else {
            holder.userLastMessage.setVisibility(View.GONE);
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
        return userMessageListItems.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfilePicture;
        CircleImageView unreadMessagesPicture;
        CircleImageView img_on;
        CircleImageView img_off;
        TextView userUsername;
        TextView userLastMessage;
        LinearLayout userContainer;
        ConstraintLayout root;
        CardView cardView;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfilePicture = itemView.findViewById(R.id.userProfilePicture);
            unreadMessagesPicture = itemView.findViewById(R.id.user_unread_message_circleImageView);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            userUsername = itemView.findViewById(R.id.user_username_textView);
            userLastMessage = itemView.findViewById(R.id.user_last_message_textView);
            userContainer = itemView.findViewById(R.id.container);
            root = itemView.findViewById(R.id.root);
            cardView = itemView.findViewById(R.id.cardView);

            if (isDark) {
                root.setBackgroundResource(R.color.black);
                userContainer.setBackgroundResource(R.color.black);
                userUsername.setTextColor(ContextCompat.getColor(context, R.color.white));
                userLastMessage.setTextColor(ContextCompat.getColor(context, R.color.white));

            } else {
                root.setBackgroundResource(R.color.white);
                userContainer.setBackgroundResource(R.color.white);
                userUsername.setTextColor(ContextCompat.getColor(context, R.color.black));
                userLastMessage.setTextColor(ContextCompat.getColor(context, R.color.black));
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
                if ("default".equals(theLastMessage)) {
                    last_msg.setText("No Message");
                } else {
                    last_msg.setText(theLastMessage);
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
