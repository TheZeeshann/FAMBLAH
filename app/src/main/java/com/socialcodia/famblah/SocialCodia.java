package com.socialcodia.famblah;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class SocialCodia extends Application {

    private static SocialCodia instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if(instance==null)
        {
            instance = this;
        }
    }

    public  static SocialCodia getInstance()
    {
        return  instance;
    }

    public static boolean isNetworkOk()
    {
        return instance.isNetworkAvailable();
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnectedOrConnecting();
    }
}
