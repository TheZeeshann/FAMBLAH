package com.socialcodia.famblah.adapter;

import android.app.AlertDialog;
import android.content.Context;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelComment;
import com.socialcodia.famblah.pojo.ResponseComment;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder>
{
    private Context context;
    private List<ModelComment> modelCommentList;
    String token;
    int userId;
    SharedPrefHandler sharedPrefHandler;
    AlertDialog.Builder builder;

    public AdapterComment(Context context, List<ModelComment> modelCommentList) {
        this.context = context;
        this.modelCommentList = modelCommentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_row,parent,false);
        sharedPrefHandler = SharedPrefHandler.getInstance(context);
        token = sharedPrefHandler.getUser().getToken();
        userId = sharedPrefHandler.getUser().getId();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelComment modelComment = modelCommentList.get(position);
        holder.tvCommentUserName.setText(modelComment.getUserName());
        holder.tvCommentTimestamp.setText(modelComment.getCommentTimestamp());
        holder.tvCommentContent.setText(modelComment.getCommentComment());
        holder.tvCommentLikesCount.setText(" "+modelComment.getCommentLikesCount());
        Boolean commentLiked = modelComment.getLiked();
        if (commentLiked)
        {
            holder.btnCommentLike.setVisibility(View.INVISIBLE);
            holder.btnCommentUnLike.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.btnCommentLike.setVisibility(View.VISIBLE);
            holder.btnCommentUnLike.setVisibility(View.INVISIBLE);
        }
        int commentId = modelComment.getCommentId();
        int commentUserId = modelComment.getUserId();

        try {
            Picasso.get().load(modelComment.getUserImage()).placeholder(R.drawable.user).into(holder.ivCommentUserProfileImage);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        holder.btnCommentLike.setOnClickListener(v -> {
            doCommentLike(holder,commentId);
        });

        holder.btnCommentUnLike.setOnClickListener(v->{
            doCommentUnLike(holder,commentId);
        });

        holder.commentConstraintLayout.setOnLongClickListener(v -> {
            showDialogue(commentId);
            return true;
        });

        holder.btnCommentLike.setOnLongClickListener(v -> {
            showDialogue(modelComment.getCommentId());
            return true;
        });

        holder.ivCommentOption.setOnClickListener(v->showCommentAction(holder,commentId,commentUserId));
    }

    private void showDialogue(int commentId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure want to delete");
        builder.setPositiveButton("Yes", (dialog, which) -> {
                deleteComment(commentId);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteComment(Integer commentId)
    {
        if (Utils.isNetworkAvailable(context))
        {
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().deleteFeedComment(token,commentId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            TastyToast.makeText(context,responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        }
                        else
                        {
                            TastyToast.makeText(context,responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        TastyToast.makeText(context,String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void showCommentAction(ViewHolder holder, int commentId, int  commentUserId)
    {
        PopupMenu popupMenu = new PopupMenu(context,holder.ivCommentOption);
        if (userId==commentUserId)
        {
            popupMenu.getMenu().add(Menu.NONE,1,1,"Edit");
            popupMenu.getMenu().add(Menu.NONE,2,2,"Delete");
        }
        popupMenu.getMenu().add(Menu.NONE,3,3,"Report");
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            switch (id)
            {
                case 1:
                    Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    showDialogue(commentId);
                    break;
                case 3:
                    Toast.makeText(context, "report", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    private void doCommentLike(ViewHolder holder, Integer commentId)
    {
        if (Utils.isNetworkAvailable(context))
        {
            holder.btnCommentLike.setVisibility(View.INVISIBLE);
            holder.btnCommentUnLike.setVisibility(View.VISIBLE);
            Call<ResponseComment> call = ApiClient.getInstance().getApi().likeFeedComment(token,commentId);
            call.enqueue(new Callback<ResponseComment>() {
                @Override
                public void onResponse(Call<ResponseComment> call, Response<ResponseComment> response) {
                    ResponseComment responseComment = response.body();
                    if (!responseComment.getError())
                    {
                        ModelComment modelComment = responseComment.getComments();
                        holder.tvCommentLikesCount.setText(String.valueOf(modelComment.getCommentLikesCount()));
                    }
                    else
                    {
                        holder.btnCommentUnLike.setVisibility(View.INVISIBLE);
                        holder.btnCommentLike.setVisibility(View.VISIBLE);
                        TastyToast.makeText(context,responseComment.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }
                @Override
                public void onFailure(Call<ResponseComment> call, Throwable t) {
                    holder.btnCommentUnLike.setVisibility(View.INVISIBLE);
                    holder.btnCommentLike.setVisibility(View.VISIBLE);
                    t.printStackTrace();
                }
            });
        }
    }

    private void doCommentUnLike(ViewHolder holder, Integer commentId)
    {
        if (Utils.isNetworkAvailable(context))
        {
            holder.btnCommentUnLike.setVisibility(View.INVISIBLE);
            holder.btnCommentLike.setVisibility(View.VISIBLE);
            Call<ResponseComment> call = ApiClient.getInstance().getApi().unlikeFeedComment(token,commentId);
            call.enqueue(new Callback<ResponseComment>() {
                @Override
                public void onResponse(Call<ResponseComment> call, Response<ResponseComment> response) {
                    ResponseComment responseComment = response.body();
                    if (!responseComment.getError())
                    {
                        ModelComment modelComment = responseComment.getComments();
                        holder.tvCommentLikesCount.setText(String.valueOf(modelComment.getCommentLikesCount()));
                    }
                    else
                    {
                        holder.btnCommentLike.setVisibility(View.INVISIBLE);
                        holder.btnCommentUnLike.setVisibility(View.VISIBLE);
                        Toast.makeText(context, responseComment.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseComment> call, Throwable t) {
                    holder.btnCommentLike.setVisibility(View.INVISIBLE);
                    holder.btnCommentUnLike.setVisibility(View.VISIBLE);
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return modelCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCommentUserName, tvCommentTimestamp, tvCommentContent, tvCommentLikesCount, btnCommentReply;
        private ImageView ivCommentUserProfileImage, ivCommentOption, btnCommentLike,btnCommentUnLike;
        private ConstraintLayout commentConstraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentUserName = itemView.findViewById(R.id.tvCommentUserName);
            tvCommentTimestamp = itemView.findViewById(R.id.tvCommentTimestamp);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            tvCommentLikesCount = itemView.findViewById(R.id.tvCommentLikesCount);
            btnCommentReply = itemView.findViewById(R.id.btnCommentReply);
            ivCommentUserProfileImage = itemView.findViewById(R.id.ivCommentUserProfileImage);
            ivCommentOption = itemView.findViewById(R.id.ivCommentOption);
            btnCommentLike = itemView.findViewById(R.id.btnCommentLike);
            btnCommentUnLike = itemView.findViewById(R.id.btnCommentUnLike);
            commentConstraintLayout = itemView.findViewById(R.id.commentConstraintLayout);
        }
    }
}
