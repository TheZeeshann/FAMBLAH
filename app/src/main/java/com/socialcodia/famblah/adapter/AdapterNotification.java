package com.socialcodia.famblah.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.activity.FeedActivity;
import com.socialcodia.famblah.activity.ProfileActivity;
import com.socialcodia.famblah.api.Api;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelNotification;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.ViewHolder> {

    private Context context;
    private List<ModelNotification> modelNotificationList;
    private String token;
    private int count;
    private SharedPrefHandler sp;
    private ModelUser modelUser;

    public AdapterNotification(Context context, List<ModelNotification> modelNotificationList) {
        this.context = context;
        this.modelNotificationList = modelNotificationList;
        this.sp = SharedPrefHandler.getInstance(context);
        this.modelUser = sp.getUser();
        this.token = modelUser.getToken();
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
        int notificationId = notification.getNotificationId();
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

        holder.notificationConstraintLayout.setOnLongClickListener(view -> {
            showDeleteNotificationAlert(notificationId);
            return false;
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

    private void showDeleteNotificationAlert(int notificationId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Notification");
        builder.setMessage("Are you sure want to delete this notification");
        builder.setPositiveButton("Delete", (dialogInterface, i) -> deleteNotification(notificationId));
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    private void deleteNotification(int notificationId)
    {
        Call<ResponseDefault> call = ApiClient.getInstance().getApi().deleteNotification(token,notificationId);
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                if (response.isSuccessful())
                {
                    ResponseDefault rd = response.body();
                    if (!rd.getError())
                        TastyToast.makeText(context,rd.getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                    else
                        TastyToast.makeText(context,rd.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                }
                else
                    TastyToast.makeText(context,String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                t.printStackTrace();
            }
        });
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
