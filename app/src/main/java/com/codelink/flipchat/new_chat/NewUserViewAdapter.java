package com.codelink.flipchat.new_chat;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codelink.flipchat.R;

import java.util.List;

public class NewUserViewAdapter extends RecyclerView.Adapter<NewUserViewAdapter.UserViewHolder> {
    private final List<UsersModelClass> usersList;

    public NewUserViewAdapter(List<UsersModelClass> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_new_user, parent, false);
        return new UserViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        UsersModelClass user = usersList.get(position);

        holder.userNameTextView.setText(user.getUserName());

        holder.userBioTextView.setText(user.getUserBio());

        Glide.with(holder.itemView.getContext())
                .load(user.getProfileUrl())
                .placeholder(R.raw.placeholder)
                .error(R.raw.placeholder)
                .into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profileImageView;
        private final TextView userNameTextView;
        private final TextView userBioTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profilePhoto);
            userNameTextView = itemView.findViewById(R.id.userName);
            userBioTextView = itemView.findViewById(R.id.userBio);
        }
    }
}
