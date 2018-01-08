package com.caminosantiago.socialway.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 17/10/2015.
 */
public class LoadImage implements  Serializable{

    @SerializedName("idUser")
    String idUser;
    @SerializedName("des")
    String des;
    @SerializedName("latitud")
    Double latitud;
    @SerializedName("longitud")
    Double longitud;
    @SerializedName("listImages")
    List<String>  listImages;


    public LoadImage(String idUser,String des,Double latitud,Double longitud,List<String>  listImages){
        this.idUser=idUser;
        this.des=des;
        this.latitud=latitud;
        this.longitud=longitud;
        this.listImages=listImages;
    }

    public String getIdUser() {
        return idUser;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public List<String> getListImages() {
        return listImages;
    }

    public String getDes() {
        return des;
    }


}
