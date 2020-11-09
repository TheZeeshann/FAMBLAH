package com.socialcodia.famblah.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.activity.MainActivity;
import com.socialcodia.famblah.adapter.AdapterUser;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseUsers;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersFragment extends Fragment {

    private RecyclerView userRecyclerView;
    private SharedPrefHandler sharedPrefHandler;
    private String token;
    private List<ModelUser> modelUserList;
    private AdapterUser adapterUser;
    private LinearLayout userLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_users, container, false);
        init(view);
        sharedPrefHandler = SharedPrefHandler.getInstance(getContext());
        token = sharedPrefHandler.getUser().getToken();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(layoutManager);
        setHasOptionsMenu(true);
        modelUserList = new ArrayList<>();

        try {
            ((MainActivity)getActivity()).getNotificationsCount();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        getUser();
        return view;
    }

    private void getUser()
    {
        Call<ResponseUsers> call = ApiClient.getInstance().getApi().getUsers(token);
        call.enqueue(new Callback<ResponseUsers>() {
            @Override
            public void onResponse(Call<ResponseUsers> call, Response<ResponseUsers> response) {
                if (response.isSuccessful())
                {
                    ResponseUsers responseUsers = response.body();
                    if (!responseUsers.getError())
                    {
                        userLinearLayout.setVisibility(View.GONE);
                        modelUserList = responseUsers.getUsers();
                        adapterUser = new AdapterUser(modelUserList,getContext());
                        userRecyclerView.setAdapter(adapterUser);
                    }
                    else
                    {
                        userLinearLayout.setVisibility(View.VISIBLE);
                        //TastyToast.makeText(getContext(),responseUsers.getMessage(),TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    }
                }
                else
                {
                    TastyToast.makeText(getContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseUsers> call, Throwable t) {
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
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
        super.onPrepareOptionsMenu(menu);
    }

    private void init(View view)
    {
        userRecyclerView = view.findViewById(R.id.usersRecyclerView);
        userLinearLayout = view.findViewById(R.id.userLinearLayout);
    }
}