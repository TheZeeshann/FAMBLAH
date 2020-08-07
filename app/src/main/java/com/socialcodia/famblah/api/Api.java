package com.socialcodia.famblah.api;


import com.socialcodia.famblah.model.response.ResponseComment;
import com.socialcodia.famblah.model.response.ResponseComments;
import com.socialcodia.famblah.model.response.ResponseDefault;
import com.socialcodia.famblah.model.response.ResponseFeed;
import com.socialcodia.famblah.model.response.ResponseFeeds;
import com.socialcodia.famblah.model.response.ResponseLogin;
import com.socialcodia.famblah.model.response.ResponseNotification;
import com.socialcodia.famblah.model.response.ResponseNotificationsCount;
import com.socialcodia.famblah.model.response.ResponseUser;
import com.socialcodia.famblah.model.response.ResponseUsers;
import com.socialcodia.famblah.storage.Constants;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

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
    @POST("likeFeedComment")
    Call<ResponseComment>  likeFeedComment(
            @Header("token")  String token,
            @Field("commentId") int feedId
    );

    @FormUrlEncoded
    @POST("unlikeFeedComment")
    Call<ResponseComment>  unlikeFeedComment(
            @Header("token")  String token,
            @Field("commentId") int commentId
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
    @POST("updateUser")
    Call<ResponseUser> updateUserWithImage(
            @Header("token") String token,
            @PartMap Map<String,RequestBody> param,
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("postFeed")
    Call<ResponseDefault> postFeed(
            @Header("token") String token,
            @Field("content") String content
    );

    @Multipart
    @POST("postFeed")
    Call<ResponseDefault> postFeedWithImage(
            @Header("token") String token,
            @PartMap Map<String, RequestBody> map,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST("updateFeed")
    Call<ResponseDefault> updateFeedWithImage(
            @Header("token") String token,
            @PartMap Map<String, RequestBody> map,
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("updateFeed")
    Call<ResponseDefault> updateFeed(
            @Header("token") String token,
            @Field("feedId") String feedId,
            @Field("content") String content
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

    @GET("notifications")
    Call<ResponseNotification> getNotifications(
            @Header("token") String token
    );

    @GET("notificationsCount")
    Call<ResponseNotificationsCount> getNotificationsCount(
            @Header("token") String token
    );

    @GET("notificationsSeened")
    Call<ResponseDefault> setNotificationsSeened(
            @Header("token") String token
    );

    @GET("feed/{id}")
    Call<ResponseFeed> getFeedById(
            @Header("token") String token,
            @Path("id") String id
    );


    @FormUrlEncoded
    @POST("postFeedComment")
    Call<ResponseComment> postFeedComment(
            @Header("token") String token,
            @Field("feedId") String feedId,
            @Field("comment") String comment
    );

    @GET("feed/{feedId}/comments")
    Call<ResponseComments> getComments(
            @Header("token") String token,
            @Path("feedId") String feedId
    );

    @FormUrlEncoded
    @POST("sendFriendRequest")
    Call<ResponseDefault> sendFriendRequest(
            @Header("token") String token,
            @Field("userId") int userId
    );

    @FormUrlEncoded
    @POST("reportFeed")
    Call<ResponseDefault> reportFeed(
            @Header("token") String token,
            @Field("feedId") int feedId
    );

    @FormUrlEncoded
    @POST("acceptFriendRequest")
    Call<ResponseDefault> acceptFriendRequest(
            @Header("token") String token,
            @Field("userId") int userId
    );

    @FormUrlEncoded
    @POST("cancelFriendRequest")
    Call<ResponseDefault> cancelFriendRequest(
            @Header("token") String token,
            @Field("userId") int userId
    );

    @FormUrlEncoded
    @POST("deleteFriend")
    Call<ResponseDefault> deleteFriend(
            @Header("token") String token,
            @Field("userId") int userId
    );

    @FormUrlEncoded
    @POST("deleteFeedComment")
    Call<ResponseDefault> deleteFeedComment(
            @Header("token") String token,
            @Field("userId") int id
    );

}
