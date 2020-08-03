package com.socialcodia.famblah.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.socialcodia.famblah.model.ModelUser;

public class SharedPrefHandler {
    private static final String SHARED_PREF_NAME = "FAMBLAH";
    private static SharedPrefHandler mInstance;
    private static SharedPreferences sharedPreferences;
    private Context context;

    public SharedPrefHandler(Context context)
    {
        this.context =context;
    }

    public static synchronized SharedPrefHandler getInstance(Context context)
    {
        if (mInstance==null)
        {
            mInstance = new SharedPrefHandler(context);
        }
        return mInstance;
    }

    public void saveUser(ModelUser user)
    {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.USER_ID,user.getId());
        editor.putString(Constants.USER_NAME,user.getName());
        editor.putString(Constants.USER_USERNAME,user.getUsername());
        editor.putString(Constants.USER_EMAIL,user.getEmail());
        editor.putString(Constants.USER_BIO,user.getBio());
        editor.putString(Constants.USER_IMAGE,user.getImage());
        editor.putString(Constants.USER_TOKEN,user.getToken());
        editor.putInt(Constants.USER_FEEDS_COUNT,user.getFeedsCount());
        editor.putInt(Constants.USER_FRIENDS_COUNT,user.getFriendsCount());
        editor.apply();
    }

    public ModelUser getUser()
    {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        ModelUser user = new ModelUser(
                sharedPreferences.getInt(Constants.USER_ID,-1),
                sharedPreferences.getInt(Constants.USER_FEEDS_COUNT,0),
                sharedPreferences.getInt(Constants.USER_FRIENDS_COUNT,0),
                sharedPreferences.getString(Constants.USER_NAME,null),
                sharedPreferences.getString(Constants.USER_USERNAME,null),
                sharedPreferences.getString(Constants.USER_EMAIL,null),
                sharedPreferences.getString(Constants.USER_BIO,null),
                sharedPreferences.getString(Constants.USER_IMAGE,null),
                sharedPreferences.getString(Constants.USER_TOKEN,null)
        );
        return user;
    }

    public Boolean isLoggedIn()
    {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        int isLogin =  sharedPreferences.getInt(Constants.USER_ID,-1);
        if (isLogin!=-1)
        {
            return true;
        }
        return false;
    }

    public void doLogout()
    {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
