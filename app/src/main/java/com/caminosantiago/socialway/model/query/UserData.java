package com.caminosantiago.socialway.model.query;

import com.google.gson.annotations.SerializedName;
import com.caminosantiago.socialway.model.Publication;
import com.caminosantiago.socialway.model.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 17/10/2015.
 */
public class UserData implements Serializable{


    @SerializedName("status")
    String status;

    @SerializedName("id")
    String id;

    @SerializedName("favourites")
    List<Integer> favourites;

    @SerializedName("userInfo")
    User userInfo;

    @SerializedName("follow")
    int follow;

    @SerializedName("listPublication")
    List<Publication> listPublication;


    public List<Integer> getFavourites() {
        return favourites;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public String getId() {
        return id;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public String getStatus() {
        return status;
    }
    public List<Publication> getListPublication() {
        return listPublication;
    }

}
