package com.caminosantiago.socialway.model.query;

import com.google.gson.annotations.SerializedName;
import com.caminosantiago.socialway.model.Comment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 17/10/2015.
 */
public class ListComments implements Serializable {

    @SerializedName("status")
    String status;

    @SerializedName("comments")
    List<Comment> comments;

    @SerializedName("idPublication")
    int idPublication;


    public int getIdPublication() {
        return idPublication;
    }

    public String getStatus() {
        return status;
    }

    public List<Comment> getComments() {
        return comments;
    }


}
