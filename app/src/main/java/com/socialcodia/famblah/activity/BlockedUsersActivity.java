package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.adapter.AdapterUser;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseUsers;
import com.socialcodia.famblah.storage.SharedPrefHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockedUsersActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private SharedPrefHandler sp;
    private RecyclerView recyclerView;
    private String token;
    private ModelUser mUser;
    private List<ModelUser> modelUserList;
    private LinearLayout blockLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users);
        init();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        actionBar.setTitle("Blocked Users");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        blockLinearLayout.setVisibility(View.GONE);

        getBlockedUsers();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void getBlockedUsers()
    {
        Call<ResponseUsers> call = ApiClient.getInstance().getApi().getBlockedUser(token);
        call.enqueue(new Callback<ResponseUsers>() {
            @Override
            public void onResponse(Call<ResponseUsers> call, Response<ResponseUsers> response) {
                if (response.isSuccessful())
                {
                    ResponseUsers rUsers = response.body();
                    if (!rUsers.getError())
                    {
                        blockLinearLayout.setVisibility(View.GONE);
                        modelUserList = rUsers.getUsers();
                        AdapterUser adapterUser = new AdapterUser(modelUserList,getApplicationContext());
                        recyclerView.setAdapter(adapterUser);
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),rUsers.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        blockLinearLayout.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseUsers> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void init()
    {
        actionBar = getSupportActionBar();
        sp = SharedPrefHandler.getInstance(getApplicationContext());
        recyclerView = findViewById(R.id.blockUserRecyclerView);
        blockLinearLayout = findViewById(R.id.blockLinearLayout);
        mUser = sp.getUser();
        token = mUser.getToken();
        modelUserList = new ArrayList<>();
    }
}