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
import com.socialcodia.famblah.activity.FeedActivity;
import com.socialcodia.famblah.activity.ProfileActivity;
import com.socialcodia.famblah.model.ModelNotification;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.ViewHolder> {

    private Context context;
    private List<ModelNotification> modelNotificationList;
    private int count;

    public AdapterNotification(Context context, List<ModelNotification> modelNotificationList) {
        this.context = context;
        this.modelNotificationList = modelNotificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_notification,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int modelListSize = modelNotificationList.size();
        ModelNotification notification = modelNotificationList.get(position);
        int notificationType = notification.getNotificationType();
        String notificationText = notification.getNotificationText();
        int verified = notification.getUserVerified();
        holder.tvUserName.setText(notification.getUserName());
        holder.tvNotificationTimestamp.setText(notification.getTimestamp());
        if (verified==0)
        {
            holder.ivUserVerified.setVisibility(View.GONE);
        }
        else
        {
            holder.ivUserVerified.setVisibility(View.VISIBLE);
        }
        try {
            Picasso.get().load(notification.getUserImage()).placeholder(R.drawable.user).into(holder.userProfileImage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        holder.tvNotificationText.setText(notificationText);

        holder.userProfileImage.setOnClickListener(v -> {
            sendToProfileActivity(notification.getUserUsername());
        });

        holder.tvUserName.setOnClickListener(v -> {
            sendToProfileActivity(notification.getUserUsername());
        });

        holder.notificationConstraintLayout.setOnClickListener(v -> {
            if (notificationType==1 || notificationType==11 || notificationType==111)
            {
                sendToFeedActivity(notification.getFeedId());
                return;
            }
            if (notificationType==2 || notificationType==4)
            {
                sendToProfileActivity(notification.getUserUsername());
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelNotificationList.size();
    }

    private void sendToProfileActivity(String username)
    {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("IntentUsername",username);
        context.startActivity(intent);
    }

    private void sendToFeedActivity(int feedId)
    {
        Intent intent = new Intent(context, FeedActivity.class);
        intent.putExtra("IntentFeedId",String.valueOf(feedId));
        context.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView userProfileImage,ivUserVerified;
        private TextView tvUserName,tvNotificationText,tvNotificationTimestamp;
        private ConstraintLayout notificationConstraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            ivUserVerified = itemView.findViewById(R.id.ivUserVerified);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvNotificationText = itemView.findViewById(R.id.tvNotificationText);
            tvNotificationTimestamp = itemView.findViewById(R.id.tvNotificationTimestamp);
            notificationConstraintLayout = itemView.findViewById(R.id.notificationConstraintLayout);
        }
    }

}
