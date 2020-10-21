package com.socialcodia.famblah.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.activity.ProfileActivity;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> implements Filterable {

    private List<ModelUser> modelUsersList;
    private List<ModelUser> modelUsersListAll;
    private Context context;
    private String token;
    private SharedPrefHandler sharedPrefHandler;
    private ModelUser mUser;

    public AdapterUser(List<ModelUser> modelUsersList, Context context) {
        this.modelUsersList = modelUsersList;
        this.context = context;
        this.modelUsersListAll = new ArrayList<>(modelUsersList);
        this.sharedPrefHandler = SharedPrefHandler.getInstance(context);
        this.mUser = sharedPrefHandler.getUser();
        this.token = mUser.getToken();
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
        String username = user.getUsername();
        int friendShipStatus = user.getFriendshipStatus();
        int status = user.getStatus();
        int hisUserId = user.getId();

        if (status==0)
        {
            holder.ivUserVerified.setVisibility(View.GONE);
        }
        else
        {
            holder.ivUserVerified.setVisibility(View.VISIBLE);
        }

        if (friendShipStatus==0)
        {
            if (mUser.getId()!=user.getId())
            {
                holder.btnAddFriend.setVisibility(View.VISIBLE);
            }
        }
        else if (friendShipStatus==1)
        {
            holder.btnAddFriend.setVisibility(View.GONE);
            holder.btnUnFriend.setVisibility(View.VISIBLE);
        }
        else if (friendShipStatus==2)
        {
            holder.btnAddFriend.setVisibility(View.GONE);
            holder.btnCancelFriendRequest.setVisibility(View.VISIBLE);
        }
        else if (friendShipStatus==3)
        {
            holder.btnAddFriend.setVisibility(View.GONE);
            holder.btnUnFriend.setVisibility(View.GONE);
            holder.btnAcceptFriendRequest.setVisibility(View.VISIBLE);
            holder.btnRejectFriendRequest.setVisibility(View.VISIBLE);
        }

        holder.tvUserName.setText(name);
        holder.tvUserEmail.setText("@"+username);
        try {
            Picasso.get().load(image).placeholder(R.drawable.user).into(holder.userProfileImage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        holder.userRowLayout.setOnClickListener(v -> {
            if (user.getId()!=mUser.getId())
            {
                sendToProfileActivity(user.getUsername());
            }
        });

        holder.btnAddFriend.setOnClickListener(v->sendFriendRequest(holder,hisUserId));

        holder.btnCancelFriendRequest.setOnClickListener(v->cancelFriendRequestAlert(holder,hisUserId));

        holder.btnAcceptFriendRequest.setOnClickListener(v-> acceptFriendRequest(holder,hisUserId));

        holder.btnRejectFriendRequest.setOnClickListener(v-> rejectFriendRequestAlert(holder,hisUserId,name));

        holder.btnUnFriend.setOnClickListener(v-> unFriendAlert(holder,hisUserId,name));

    }

    private void cancelFriendRequestAlert(ViewHolder holder, int hisUserId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cancel Friend Request");
        builder.setMessage("Are you sure want to cancel friend request");
        builder.setPositiveButton("Yes", (dialog, which) -> cancelFriendRequest(holder,hisUserId));
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void rejectFriendRequestAlert(ViewHolder holder, int hisUserId, String name)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reject Friend Request");
        builder.setMessage("Are you sure want to reject the friend request of "+name);
        builder.setPositiveButton("Yes", (dialog, which) -> rejectFriendRequest(holder,hisUserId));
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void unFriendAlert(ViewHolder holder, int hisUserId, String name)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("UnFriend");
        builder.setMessage("Are you sure want to delete friendship with "+name);
        builder.setPositiveButton("Yes", (dialog, which) -> unFriend(holder,hisUserId));
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
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

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ModelUser> filteredList = new ArrayList<>();
            if (constraint == null || constraint.toString().toLowerCase().trim().length() == 0)
            {
                filteredList.addAll(modelUsersListAll);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ModelUser user : modelUsersListAll)
                {
                    if (user.getName().toLowerCase().contains(filterPattern) || user.getUsername().toLowerCase().contains(filterPattern) || user.getEmail().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(user);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            modelUsersList.clear();;
            modelUsersList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUserName, tvUserEmail;
        private ImageView userProfileImage,ivUserVerified;
        private Button btnAddFriend, btnUnFriend, btnCancelFriendRequest,btnAcceptFriendRequest,btnRejectFriendRequest;
        private ConstraintLayout userRowLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            ivUserVerified = itemView.findViewById(R.id.ivUserVerified);
            userRowLayout = itemView.findViewById(R.id.userRowLayout);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
            btnUnFriend = itemView.findViewById(R.id.btnUnFriend);
            btnCancelFriendRequest = itemView.findViewById(R.id.btnCancelFriendRequest);
            btnAcceptFriendRequest = itemView.findViewById(R.id.btnAcceptFriendRequest);
            btnRejectFriendRequest = itemView.findViewById(R.id.btnRejectFriendRequest);
        }
    }

    private void rejectFriendRequest(ViewHolder holder,int hisUserId)
    {
        if (Utils.isNetworkAvailable(context))
        {
            holder.btnRejectFriendRequest.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().cancelFriendRequest(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            TastyToast.makeText(context,"Friendship Request Rejected",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                            holder.btnRejectFriendRequest.setEnabled(true);
                            holder.btnRejectFriendRequest.setVisibility(View.GONE);
                            holder.btnAcceptFriendRequest.setVisibility(View.GONE);
                            holder.btnAddFriend.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            holder.btnRejectFriendRequest.setEnabled(true);
                            TastyToast.makeText(context,responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        holder.btnRejectFriendRequest.setEnabled(true);
                        TastyToast.makeText(context,String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    holder.btnRejectFriendRequest.setEnabled(true);
                    t.printStackTrace();
                }
            });
        }
    }

    private void acceptFriendRequest(ViewHolder holder,int hisUserId)
    {
        if (Utils.isNetworkAvailable(context))
        {
            holder.btnAcceptFriendRequest.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().acceptFriendRequest(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            TastyToast.makeText(context,"Friend Request Accepted",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                            holder.btnAcceptFriendRequest.setEnabled(true);
                            holder.btnRejectFriendRequest.setVisibility(View.GONE);
                            holder.btnAcceptFriendRequest.setVisibility(View.GONE);
                            holder.btnUnFriend.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            holder.btnAcceptFriendRequest.setEnabled(true);
                            TastyToast.makeText(context,responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        holder.btnAcceptFriendRequest.setEnabled(true);
                        TastyToast.makeText(context,String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    holder.btnAcceptFriendRequest.setEnabled(true);
                    t.printStackTrace();
                }
            });
        }
    }

    private void cancelFriendRequest(ViewHolder holder, int hisUserId)
    {
        if (Utils.isNetworkAvailable(context))
        {
            holder.btnCancelFriendRequest.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().cancelFriendRequest(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            TastyToast.makeText(context,"Friendship Request Canceled",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                            holder.btnCancelFriendRequest.setEnabled(true);
                            holder.btnCancelFriendRequest.setVisibility(View.GONE);
                            holder.btnAddFriend.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            holder.btnCancelFriendRequest.setEnabled(true);
                            TastyToast.makeText(context,responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        holder.btnCancelFriendRequest.setEnabled(true);
                        TastyToast.makeText(context,String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    holder.btnCancelFriendRequest.setEnabled(true);
                    t.printStackTrace();
                }
            });
        }
    }

    private void unFriend(ViewHolder holder, int hisUserId)
    {
        if (Utils.isNetworkAvailable(context))
        {
            holder.btnUnFriend.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().deleteFriend(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            TastyToast.makeText(context,"Friendship Deleted",TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            holder.btnUnFriend.setEnabled(true);
                            holder.btnUnFriend.setVisibility(View.GONE);
                            holder.btnAddFriend.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            holder.btnUnFriend.setEnabled(true);
                            TastyToast.makeText(context,responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        holder.btnUnFriend.setEnabled(true);
                        TastyToast.makeText(context,String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    holder.btnUnFriend.setEnabled(true);
                    t.printStackTrace();
                }
            });
        }
    }

    private void sendFriendRequest(ViewHolder holder, int hisUserId)
    {
        if (Utils.isNetworkAvailable(context))
        {
            holder.btnAddFriend.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().sendFriendRequest(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            holder.btnAddFriend.setEnabled(true);
                            holder.btnAddFriend.setVisibility(View.GONE);
                            holder.btnCancelFriendRequest.setVisibility(View.VISIBLE);
                            TastyToast.makeText(context,"Friend Request Sent",TastyToast.LENGTH_LONG,TastyToast.ERROR);
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
                    holder.btnAddFriend.setEnabled(true);
                    t.printStackTrace();
                }
            });
        }
    }
}
