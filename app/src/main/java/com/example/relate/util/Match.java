package com.example.relate.util;

public class Match {
    String name;
    String user_id;
    String Img_url;

    public Match(){

    }

    public String getImg_url(){
        return Img_url;
    }

    public void setImg_url(String Img_url){
        this.Img_url = Img_url;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getUser_id(){
        return user_id;
    }

    public void setUser_id(String user_id){
        this.user_id = user_id;
    }
}
