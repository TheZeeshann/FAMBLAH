package com.socialcodia.famblah.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.socialcodia.famblah.activity.FeedActivity;
import com.socialcodia.famblah.activity.ProfileActivity;
import com.socialcodia.famblah.model.ModelFeed;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelFeed;
import com.socialcodia.famblah.model.ResponseDefault;
import com.socialcodia.famblah.model.ResponseFeed;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.fragment.ProfileFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.ViewHolder> {

    List<ModelFeed> modelFeedList;
    Context context;


    public AdapterFeed(List<ModelFeed> modelFeedList, Context context) {
        this.modelFeedList = modelFeedList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        ModelFeed feed = modelFeedList.get(position);
        String username = modelFeedList.get(position).getUserUsername();
        holder.tvUserName.setText(feed.getUserName());
        holder.tvFeedTimestamp.setText(feed.getFeedTimestamp());
        holder.tvFeedContent.setText(feed.getFeedContent());
        String likeCounts = feed.getFeedLikes().toString();
        String commentCounts = feed.getFeedComments().toString();
        String feedId = feed.getFeedId().toString();
        String feedImage = feed.getFeedImage();
        String feedUserImage = feed.getUserImage();
        String feedUserId = feed.getUserId().toString();
        Boolean liked = feed.getLiked();
        holder.tvFeedLike.setText(likeCounts+" Likes");
        holder.tvFeedComment.setText(commentCounts+" Comments");
        if (liked)
        {
            holder.tvLike.setVisibility(View.INVISIBLE);
            holder.tvUnlike.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.tvUnlike.setVisibility(View.INVISIBLE);
            holder.tvLike.setVisibility(View.VISIBLE);
        }
        if (!feedImage.isEmpty())
        {
            try {
                Picasso.get().load(feedImage).into(holder.ivFeedImage);
            }
            catch (Exception e)
            {
                Toast.makeText(context, "Image Error " +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            holder.ivFeedImage.setVisibility(View.GONE);
        }

        try {
            Picasso.get().load(feedUserImage).into(holder.userProfileImage);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        holder.tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLike(feedId,likeCounts,holder);
            }
        });

        holder.tvUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDislike(feedId,likeCounts,holder);
            }
        });

        holder.ivFeedOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedActionOptions(holder.ivFeedOption,feedId,feedUserId);
            }
        });

        holder.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedUserId.equals(String.valueOf(SharedPrefHandler.getInstance(context).getUser().getId())))
                {
                    sendToProfileFragment();
                }
                else
                {
                    sendToProfileActivity(username);
                }
            }
        });

        holder.tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedUserId.equals(String.valueOf(SharedPrefHandler.getInstance(context).getUser().getId())))
                {
                    sendToProfileFragment();
                }
                else
                {
                    sendToProfileActivity(username);
                }
            }
        });

        holder.tvFeedContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFeedActivity(feedId);
            }
        });

        holder.tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFeedActivity(feedId);
            }
        });
    }

    private void sendToFeedActivity(String feedId)
    {
        Intent intent = new Intent(context, FeedActivity.class);
        intent.putExtra("IntentFeedId",feedId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void sendToProfileActivity(String username)
    {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("IntentUsername",username);
        context.startActivity(intent);
    }

    private void sendToProfileFragment()
    {
        Fragment fragment = new ProfileFragment();
        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
    }


    private void showFeedActionOptions(ImageView ivFeedOption, String feedId, String userId)
    {
        PopupMenu popupMenu = new PopupMenu(context,ivFeedOption);
        String id = String.valueOf(SharedPrefHandler.getInstance(context).getUser().getId());
        if (userId.equals(id))
        {
            popupMenu.getMenu().add(Menu.NONE,0,0,"Edit");
            popupMenu.getMenu().add(Menu.NONE,1,1,"Delete");
        }
        popupMenu.getMenu().add(Menu.NONE,2,2,"Share");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                    if (id==0){
                        Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
                    }
                    else if (id==1)
                    {
                        showAlertDialog(feedId);
                    }
                    else if (id==2)
                    {
                        Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
                    }
                return  false;
            }
        });
        popupMenu.show();
    }

    private boolean showAlertDialog(String feedId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure want to delete");
        //Delete Button
        builder.setPositiveButton("Yes", (dialog, which) -> deleteFeed(feedId));
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }


    private void deleteFeed(String feedId)
    {
        Call<ResponseDefault> call = ApiClient.getInstance().getApi().deleteFeed(SharedPrefHandler.getInstance(context).getUser().getToken(),feedId);
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                if (response.isSuccessful())
                {
                   ResponseDefault ResponseDefault = response.body();
                   if (!ResponseDefault.getError())
                   {
                       Toast.makeText(context, ResponseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                   }
                   else
                   {
                       Toast.makeText(context, ResponseDefault.getMessage(), Toast.LENGTH_SHORT).show();

                   }
                }
                else
                {
                    Toast.makeText(context, "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void doLike(String feedId, String likeCounts,ViewHolder holder)
    {
        int like = Integer.parseInt((likeCounts));
        holder.tvLike.setVisibility(View.INVISIBLE);
        holder.tvUnlike.setVisibility(View.VISIBLE);
        String token = SharedPrefHandler.getInstance(context).getUser().getToken();
        Call<ResponseFeed> call = ApiClient.getInstance().getApi().doLike(token,Integer.parseInt(feedId));
        call.enqueue(new Callback<ResponseFeed>() {
            @Override
            public void onResponse(Call<ResponseFeed> call, Response<ResponseFeed> response) {
                ResponseFeed responseFeed = response.body();
                if (!responseFeed.getError())
                {
                    ModelFeed modelFeed = responseFeed.getFeed();
                    holder.tvFeedLike.setText(modelFeed.getFeedLikes()+" Likes");
                }
                else
                {
                    Toast.makeText(context, "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseFeed> call, Throwable t) {
                holder.tvUnlike.setVisibility(View.INVISIBLE);
                holder.tvLike.setVisibility(View.VISIBLE);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doDislike(String feedId, String likeCounts,ViewHolder holder)
    {
        holder.tvUnlike.setVisibility(View.INVISIBLE);
        holder.tvLike.setVisibility(View.VISIBLE);
        int like = Integer.parseInt((likeCounts));
        String token = SharedPrefHandler.getInstance(context).getUser().getToken();
        Call<ResponseFeed> call = ApiClient.getInstance().getApi().doDislike(token,Integer.parseInt(feedId));
        call.enqueue(new Callback<ResponseFeed>() {
            @Override
            public void onResponse(Call<ResponseFeed> call, Response<ResponseFeed> response) {
                ResponseFeed responseFeed = response.body();
                if (!responseFeed.getError())
                {
                    ModelFeed modelFeed = responseFeed.getFeed();
                    holder.tvFeedLike.setText(modelFeed.getFeedLikes()+" Likes");
                }
                else
                {
                    Toast.makeText(context, "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseFeed> call, Throwable t) {
                holder.tvLike.setVisibility(View.INVISIBLE);
                holder.tvUnlike.setVisibility(View.VISIBLE);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelFeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private ImageView userProfileImage,ivFeedImage,ivFeedOption;
        private TextView tvUserName, tvFeedTimestamp,tvFeedContent,tvFeedLike,tvFeedComment,tvComment,tvLike,tvUnlike,tvShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            ivFeedImage = itemView.findViewById(R.id.ivFeedImage);
            ivFeedOption = itemView.findViewById(R.id.ivFeedOption);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvFeedTimestamp = itemView.findViewById(R.id.tvFeedTimestamp);
            tvFeedContent = itemView.findViewById(R.id.tvFeedContent);
            tvFeedLike = itemView.findViewById(R.id.tvFeedLike);
            tvFeedComment = itemView.findViewById(R.id.tvFeedComment);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvLike = itemView.findViewById(R.id.tvLike);
            tvUnlike = itemView.findViewById(R.id.tvUnlike);
            tvShare = itemView.findViewById(R.id.tvShare);

        }
    }

}
