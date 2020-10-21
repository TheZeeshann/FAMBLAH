package com.socialcodia.famblah.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;

public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo!=null)
            return true;
        TastyToast.makeText(context,"No Internet Connection",TastyToast.LENGTH_LONG,TastyToast.ERROR);
        return false;
    }

}
