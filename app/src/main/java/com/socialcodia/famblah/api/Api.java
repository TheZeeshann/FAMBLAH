package com.socialcodia.famblah.api;

import com.socialcodia.famblah.model.ResponseDefault;
import com.socialcodia.famblah.model.ResponseLogin;
import com.socialcodia.famblah.storage.Constants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api
{
    @FormUrlEncoded
    @POST("login")
    Call<ResponseLogin> login(
            @Field(Constants.USER_EMAIL) String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register")
    Call<ResponseDefault> register(
            @Field(Constants.USER_NAME) String name,
            @Field(Constants.USER_USERNAME) String username,
            @Field(Constants.USER_EMAIL) String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("forgotPassword")
    Call<ResponseDefault> forgotPassword(@Field(Constants.USER_EMAIL) String email);

    @FormUrlEncoded
    @POST("resetPassword")
    Call<ResponseDefault> resetPassword(
            @Field(Constants.USER_EMAIL) String email,
            @Field("otp") String otp,
            @Field("newPassword") String password
    );

}
