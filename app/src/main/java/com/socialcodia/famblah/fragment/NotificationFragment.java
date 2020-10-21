package com.socialcodia.famblah.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.activity.MainActivity;
import com.socialcodia.famblah.adapter.AdapterNotification;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelNotification;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.pojo.ResponseNotification;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private RecyclerView notificationRecyclerView;
    private SharedPrefHandler sharedPrefHandler;
    private String token;
    private List<ModelNotification> modelNotificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        init(view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        notificationRecyclerView.setLayoutManager(layoutManager);
        setHasOptionsMenu(true);
        getNotifications();
        setNotificationSeened();
        return view;
    }

    public void getNotifications()
    {
        Call<ResponseNotification> call = ApiClient.getInstance().getApi().getNotifications(token);
        call.enqueue(new Callback<ResponseNotification>() {
            @Override
            public void onResponse(Call<ResponseNotification> call, Response<ResponseNotification> response) {
                if (response.isSuccessful())
                {
                    ResponseNotification responseNotification = response.body();
                    if (!responseNotification.getError())
                    {
                        modelNotificationList = responseNotification.getNotifications();
                        AdapterNotification adapterNotification = new AdapterNotification(getContext(),modelNotificationList);
                        notificationRecyclerView.setAdapter(adapterNotification);
                    }
                    else
                    {
                        TastyToast.makeText(getContext(),responseNotification.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }
                else
                {
                    TastyToast.makeText(getContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseNotification> call, Throwable t) {

            }
        });
    }

    private void setNotificationSeened()
    {
        if (Utils.isNetworkAvailable(getContext()))
        {
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().setNotificationsSeened(token);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            sharedPrefHandler.saveNotificationsCount(0);
                            try {
                                ((MainActivity)getActivity()).setNotificationsBadge();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            TastyToast.makeText(getContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        TastyToast.makeText(getContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void init(View view)
    {
        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView);
        sharedPrefHandler = SharedPrefHandler.getInstance(getContext());
        ModelUser user = sharedPrefHandler.getUser();
        token = user.getToken();
        modelNotificationList = new ArrayList<>();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.miSearch).setVisible(false);
        menu.findItem(R.id.miPostFeed).setVisible(false);
        menu.findItem(R.id.miSettings).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}