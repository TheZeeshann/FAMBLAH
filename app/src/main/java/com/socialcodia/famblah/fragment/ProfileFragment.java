package com.socialcodia.famblah.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.activity.EditProfileActivity;
import com.socialcodia.famblah.activity.MainActivity;
import com.socialcodia.famblah.adapter.AdapterFeed;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelFeed;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.model.response.ResponseFeeds;
import com.socialcodia.famblah.model.response.ResponseUser;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvUserName,tvUserUsername,tvUserBio,tvFeedsCount,tvFriendsCount;
    private ImageView userProfileImage;
    private Button btnEditProfile;

    private RecyclerView recyclerView;
    List<ModelFeed> modelFeedList;

    String token,username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        setHasOptionsMenu(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        modelFeedList = new ArrayList<>();

        try {
            ((MainActivity)getActivity()).getNotificationsCount();
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        ModelUser modelUser = SharedPrefHandler.getInstance(getContext()).getUser();
        token = modelUser.getToken();
        username = modelUser.getUsername();
        tvUserName.setText(modelUser.getName());
        tvUserUsername.setText("@"+username);
        tvUserBio.setText(modelUser.getBio());
        tvFeedsCount.setText(String.valueOf(modelUser.getFeedsCount()));
        tvFriendsCount.setText(String.valueOf(modelUser.getFriendsCount()));
        try {
            Picasso.get().load(modelUser.getImage()).placeholder(R.drawable.user).into(userProfileImage);
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        getPostByUsername();
        getUser();

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToEditProfile();
            }
        });

        return view;
    }

    private void init(View view)
    {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserUsername = view.findViewById(R.id.tvUserUsername);
        userProfileImage = view.findViewById(R.id.userProfileImage);
        recyclerView = view.findViewById(R.id.feedRecyclerView);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvUserBio = view.findViewById(R.id.tvUserBio);
        tvFeedsCount = view.findViewById(R.id.tvFeedsCount);
        tvFriendsCount = view.findViewById(R.id.tvFriendsCount);
    }

    private void getUser()
    {
        if (Utils.isNetworkAvailable(getContext()))
        {
            Call<ResponseUser> call = ApiClient.getInstance().getApi().getMyProfile(token);
            call.enqueue(new Callback<ResponseUser>() {
                @Override
                public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                    if (response.isSuccessful())
                    {
                        ResponseUser responseUser = response.body();
                        if (!responseUser.getError())
                        {
                            ModelUser modelUser = responseUser.getUser();
                            modelUser.setToken(token);
                            SharedPrefHandler.getInstance(getContext()).saveUser(modelUser);
                        }
                        else
                        {
                            Toast.makeText(getContext(), responseUser.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseUser> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendToEditProfile()
    {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void getPostByUsername()
    {
        if (Utils.isNetworkAvailable(getContext()))
        {
            Call<ResponseFeeds> call = ApiClient.getInstance().getApi().getUserFeeds(username,token);
            call.enqueue(new Callback<ResponseFeeds>() {
                @Override
                public void onResponse(Call<ResponseFeeds> call, Response<ResponseFeeds> response) {
                    if (response.isSuccessful())
                    {
                        ResponseFeeds responseFeeds = response.body();
                        if (!responseFeeds.getError())
                        {
                            modelFeedList = responseFeeds.getFeeds();
                            AdapterFeed adapterFeed = new AdapterFeed(modelFeedList,getContext());
                            recyclerView.setAdapter(adapterFeed);
                        }
                        else
                        {
                            Toast.makeText(getContext(), responseFeeds.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseFeeds> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.miPostFeed).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}