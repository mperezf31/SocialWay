package com.caminosantiago.socialway.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by root on 17/10/2015.
 */
public class User implements Serializable{


    @SerializedName("id")
    String id;

    @SerializedName("imageAvatar")
    String imageAvatar;

    @SerializedName("imageFondo")
    String imageFondo;

    @SerializedName("name")
    String name;

    @SerializedName("estado")
    String estado;

    @SerializedName("follow")
    int follow;

    @SerializedName("token")
    String token;

    @SerializedName("lastConexion")
    String lastConexion;

    public String getLastConexion() {
        return lastConexion;
    }

    public String getToken() {
        return token;
    }

    public int getFollow() {
        return follow;
    }

    public String getImageFondo() {
        return imageFondo;
    }

    public String getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getName() {
        return name;
    }

    public String getImageAvatar() {
        return imageAvatar;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }
}
