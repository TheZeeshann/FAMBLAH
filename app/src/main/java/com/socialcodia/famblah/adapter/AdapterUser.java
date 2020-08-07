package com.socialcodia.famblah.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.activity.ProfileActivity;
import com.socialcodia.famblah.model.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> {

    private List<ModelUser> modelUsersList;
    private Context context;

    public AdapterUser(List<ModelUser> modelUsersList, Context context) {
        this.modelUsersList = modelUsersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelUser user = modelUsersList.get(position);
        String name = user.getName();
        String email = user.getEmail();
        String image = user.getImage();

        holder.tvUserName.setText(name);
        holder.tvUserEmail.setText(email);
        try {
            Picasso.get().load(image).placeholder(R.drawable.user).into(holder.userProfileImage);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        holder.userRowLayout.setOnClickListener(v -> {
            sendToProfileActivity(user.getUsername());
        });

    }


    private void sendToProfileActivity(String username)
    {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("IntentUsername",username);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return modelUsersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUserName, tvUserEmail;
        private ImageView userProfileImage;
        private ConstraintLayout userRowLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userRowLayout = itemView.findViewById(R.id.userRowLayout);
        }
    }
}
