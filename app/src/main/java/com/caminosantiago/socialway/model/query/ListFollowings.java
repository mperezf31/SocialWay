package com.caminosantiago.socialway.model.query;

import com.google.gson.annotations.SerializedName;
import com.caminosantiago.socialway.model.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 17/10/2015.
 */
public class ListFollowings implements Serializable {

    @SerializedName("status")
    String status;

    @SerializedName("users")
    List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public String getStatus() {
        return status;
    }
}
