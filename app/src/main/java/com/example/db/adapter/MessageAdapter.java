package com.example.db.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.db.R;
import com.example.db.entity.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    protected static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private FirebaseUser firebaseUser;

    private Context context;
    private List<Message> chatList;
    private String imageUrl;

    private boolean isDark = false;

    public MessageAdapter(Context context, List<Message> messagesListItems, String imageUrl, boolean isDark) {
        this.chatList = messagesListItems;
        this.context = context;
        this.imageUrl = imageUrl;
        this.isDark = isDark;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message chat = chatList.get(position);

        holder.show_message.setText(chat.getMessage());

        Picasso.get().load(imageUrl).into(holder.circleImageViewUserProfilePicture);

        if (position == chatList.size()-1){
            // Check for last message
            if (chat.isIsSeen()){
                holder.text_seen.setText("Seen");
            } else {
                holder.text_seen.setText("Delivered");
            }
        } else {
            holder.text_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageViewUserProfilePicture;
        TextView show_message;
        TextView text_seen;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageViewUserProfilePicture = itemView.findViewById(R.id.profile_image);
            show_message = itemView.findViewById(R.id.show_message);
            text_seen = itemView.findViewById(R.id.txt_seen);

//            if (isDark) {
//                container.setBackgroundResource(R.drawable.card_dark_background);
//            } else {
//                container.setBackgroundResource(R.drawable.card_light_background);
//            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
