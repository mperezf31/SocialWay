package com.caminosantiago.socialway;

import com.caminosantiago.socialway.model.ResultAyuda;
import com.caminosantiago.socialway.model.query.ListComments;
import com.caminosantiago.socialway.model.query.ListFavourites;
import com.caminosantiago.socialway.model.query.ListFollowings;
import com.caminosantiago.socialway.model.query.ListPublications;
import com.caminosantiago.socialway.model.LoadImage;
import com.caminosantiago.socialway.model.ResultWS;
import com.caminosantiago.socialway.model.query.UserData;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by root on 19/10/2015.
 */
public interface  MyApiEndpointInterface {


    @FormUrlEncoded
    @POST("RegisterUser.php")
    Call<ResultWS> createUser(@Field("id") String id,@Field("email") String email,@Field("name") String name,@Field("apellido") String apellido,@Field("locale") String locale);

    @POST("SendPublication.php")
    Call<ResultWS> sendPublication(@Body LoadImage data);

    @GET("GetPublications.php")
    Call<ListPublications> getPublications( @Query("idUser") String user, @Query("lastConexion") String lastConexion,@Query("token") String tokenUser);

    @GET("GetUserData.php")
    Call<UserData> getUserData(@Query("token") String myToken, @Query("myUser") String user, @Query("idUser") String idUser);

    @FormUrlEncoded
    @POST("AddFavourite.php")
    Call<ResultWS> addFavourite(@Field("idUser") String idUser,@Field("idPublication") int idPublication);

    @FormUrlEncoded
    @POST("DeleteFavourite.php")
    Call<ResultWS> deleteFavourite(@Field("idUser") String idUser,@Field("idPublication") int idPublication);

    @GET("GetComments.php")
    Call<ListComments> getComments(@Query("idPublication") int idPublication);

    @FormUrlEncoded
    @POST("AddComments.php")
    Call<ListComments> addComments(@Field("idUser") String idUser,@Field("idPublication") int idPublication,@Field("texto") String msg);

    @GET("GetFavourites.php")
    Call<ListFavourites> getFavourites(@Query("idPublication") int idPublication);


    @POST("Share.php")
    Call<ResultWS> sharePublication(@Body LoadImage data);

    @FormUrlEncoded
    @POST("SetAvatar.php")
    Call<ResultWS> setAvatar(@Field("idUser") String idUser, @Field("image") String data);

    @FormUrlEncoded
    @POST("SetImagenFondo.php")
    Call<ResultWS> setImagenFondo(@Field("idUser") String idUser, @Field("image") String data);

    @FormUrlEncoded
    @POST("SetTextsUser.php")
    Call<ResultWS> setTezts(@Field("idUser") String idUser,@Field("name") String nombre,@Field("estado") String estado);

    @FormUrlEncoded
    @POST("AddFollow.php")
    Call<ResultWS> addFollow(@Field("idUser") String idUser,@Field("userFollow") String idUser1);

    @FormUrlEncoded
    @POST("RemoveFollow.php")
    Call<ResultWS> removeFollow(@Field("idUser") String idUser,@Field("userFollow") String idUser1);

    @GET("GetFollowings.php")
    Call<ListFollowings> getFollowings(@Query("idUser") String idUser);

    @GET("GetPublicationsFollowings.php")
    Call<ListPublications> getPublicationsFollowings(@Query("idUser") String idUser);

    @GET("GetInfo.php")
    Call<ResultAyuda> getInfoApp();

    @GET("GetFollowers.php")
    Call<ListFollowings> getFollowers(@Query("idUser") String idUser);

    @GET("SetWay.php")
    Call<ResultWS> setWay(@Query("idUser") String idUser,@Query("camino")  String camino);

    @GET("GetPublicationsWay.php")
    Call<ListPublications> getPublicationsWay(@Query("idUser") String idUser,@Query("camino") String camino);

    @GET("GetAllUsers.php")
    Call<ListFollowings> getAllUsers(@Query("idUser") String idUser);

    @GET("GetUserDataChat.php")
    Call<UserData> getUserDataChat(@Query("idMyUser") String idMyUser,  @Query("idUser") String idUser,@Query("lastConexion") String idUserToChat);
}
