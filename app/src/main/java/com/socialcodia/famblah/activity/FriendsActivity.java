package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.adapter.AdapterUser;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseFriends;
import com.socialcodia.famblah.pojo.ResponseUser;
import com.socialcodia.famblah.pojo.ResponseUsers;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Intent intent;
    private RecyclerView recyclerView;
    private SharedPrefHandler sharedPrefHandler;
    private String token, userId, username;
    private List<ModelUser> modelUserList;
    private ImageView imgNoFriendsFound;
    AdapterUser adapterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        init();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        if (intent.getStringExtra("intentUsername")!=null)
        {
            username = intent.getStringExtra("intentUsername");
        }
        else
        {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        getFriend();
    }

    private void init()
    {
        recyclerView = findViewById(R.id.friendsReyclerView);
        imgNoFriendsFound = findViewById(R.id.imgNoFriendsFound);
        intent = getIntent();
        actionBar = getSupportActionBar();
        actionBar.setTitle("Friends");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        sharedPrefHandler = SharedPrefHandler.getInstance(getApplicationContext());
        ModelUser modelUser = sharedPrefHandler.getUser();
        token = modelUser.getToken();
        modelUserList = new ArrayList<>();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void getFriend()
    {
        Call<ResponseFriends> call = ApiClient.getInstance().getApi().getFriends(token,username);
        call.enqueue(new Callback<ResponseFriends>() {
            @Override
            public void onResponse(Call<ResponseFriends> call, Response<ResponseFriends> response) {
                if (response.isSuccessful())
                {
                    ResponseFriends  responseFriends = response.body();
                    if (!responseFriends.isError())
                    {
                        modelUserList = responseFriends.getFriends();
                        adapterUser = new AdapterUser(modelUserList,FriendsActivity.this);
                        recyclerView.setAdapter(adapterUser);
                    }
                    else
                    {
                        if (responseFriends.getMessage().toLowerCase().equals("no friend found"))
                        {
                            imgNoFriendsFound.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(FriendsActivity.this, responseFriends.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(FriendsActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseFriends> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.miPostFeed).setVisible(false);
        menu.findItem(R.id.miSettings).setVisible(false);
        MenuItem search = menu.findItem(R.id.miSearch);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterUser.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}