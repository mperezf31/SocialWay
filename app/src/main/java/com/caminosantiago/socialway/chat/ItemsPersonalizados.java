package com.caminosantiago.socialway.chat;

public class ItemsPersonalizados  {
 
    private String title;
    private String description;
    String date;
 
    public ItemsPersonalizados(String title, String description,String date) {
        super();
        this.title = title;
        this.description = description;
        this.date=date;
    }

    public String getDate() {
        return date;
    }

    public String getTitle(){
    	return title;
    }
    public String getDescription(){
    	return description;
    }
}