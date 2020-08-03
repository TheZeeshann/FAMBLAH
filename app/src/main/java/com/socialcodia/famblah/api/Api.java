package com.socialcodia.famblah.api;


import com.socialcodia.famblah.model.ResponseComment;
import com.socialcodia.famblah.model.ResponseDefault;
import com.socialcodia.famblah.model.ResponseFeed;
import com.socialcodia.famblah.model.ResponseFeeds;
import com.socialcodia.famblah.model.ResponseLogin;
import com.socialcodia.famblah.model.ResponseUser;
import com.socialcodia.famblah.model.ResponseUsers;
import com.socialcodia.famblah.storage.Constants;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("users")
    Call<ResponseUsers> getUsers(
            @Header("token") String token
    );

    @FormUrlEncoded
    @POST("updatePassword")
    Call<ResponseDefault> updatePassword(
            @Header("token") String token,
            @Field("password") String password,
            @Field("newpassword") String newPassword
    );

    @FormUrlEncoded
    @POST("sendEmailVerfication")
    Call<ResponseDefault> sendEmailVerfication(
            @Field("email") String email
    );

    @GET("feeds")
    Call<ResponseFeeds> getFeeds(
            @Header("token") String token
    );

    @FormUrlEncoded
    @POST("likeFeed")
    Call<ResponseFeed>  doLike(
            @Header("token")  String token,
            @Field("feedId") int feedId
    );

    @FormUrlEncoded
    @POST("unlikeFeed")
    Call<ResponseFeed> doDislike(
            @Header("token") String token,
            @Field("feedId") int feedId
    );

    @FormUrlEncoded
    @POST("deleteFeed")
    Call<ResponseDefault> deleteFeed(
            @Header("token") String token,
            @Field("id") String feedId
    );

    @GET("{username}/feeds")
    Call<ResponseFeeds> getUserFeeds(
            @Path("username") String username,
            @Header("token") String token
    );

    @FormUrlEncoded
    @POST("updateUser")
    Call<ResponseUser> updateUser(
            @Header("token") String token,
            @Field("name") String name,
            @Field("username") String username,
            @Field("bio") String bio
//            @Field("image") String image
    );

    @Multipart
    @POST("postFeed")
    Call<ResponseDefault> postFeed(
            @Header("token") String token,
            @Query("content") String content,
            @Part MultipartBody.Part image
    );

    @GET("user/{username}")
    Call<ResponseUser> getUserByUsername(
            @Header("token") String token,
            @Path("username") String username
    );

    @GET("user")
    Call<ResponseUser> getMyProfile(
            @Header("token") String token
    );

    @GET("feed/{id}")
    Call<ResponseFeed> getFeedById(
            @Header("token") String token,
            @Path("id") String id
    );


    @FormUrlEncoded
    @POST("postFeedComment")
    Call<ResponseDefault> postFeedComment(
            @Header("token") String token,
            @Field("feedId") String feedId,
            @Field("comment") String comment
    );

    @GET("feed/{feedId}/comments")
    Call<ResponseComment> getComments(
            @Header("token") String token,
            @Path("feedId") String feedId
    );

}
