package com.example.db.activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.db.R;
import com.example.db.entity.User;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewConfig {
    private Context context;
    private UserAdapter usersAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<User> users, List<String> keys) {
        this.context = context;
        this.usersAdapter = new UserAdapter(users, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(usersAdapter);
    }

    class UserItemView extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView email;

        private String key;

        public UserItemView(ViewGroup parent) {
            super(LayoutInflater.from(context).
                    inflate(R.layout.users, parent, false));

            username = itemView.findViewById(R.id.usernameTextView);
            email = itemView.findViewById(R.id.emailTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UpdateProfileActivity.class);
                    intent.putExtra("key", key);
                    System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||" + key);
                    intent.putExtra("username", username.getText().toString());
                    intent.putExtra("email", email.getText().toString());

                    context.startActivity(intent);
                }
            });
        }

        public void bind(User user, String key) {
            username.setText(user.getUsername());
            username.setText(user.getEmail());

            this.key = key;
        }
    }


    class UserAdapter extends RecyclerView.Adapter<UserItemView> {
        private List<User> users = new ArrayList<>();
        private List<String> keys = new ArrayList<>();

        public UserAdapter(List<User> users, List<String> keys) {
            this.users = users;
            this.keys = keys;
        }

        @NonNull
        @Override
        public UserItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull UserItemView holder, int position) {
            holder.bind(users.get(position), keys.get(position));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
}
