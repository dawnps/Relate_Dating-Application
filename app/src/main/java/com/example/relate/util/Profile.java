package com.example.relate.util;

public class Profile extends ProfileId{

    private String Name;

    private String Img_url;

    private Integer Age;

    //private String location;

    public String getImg_url(){
        return Img_url;
    }

    public void setImg_url(String img_url){
        this.Img_url = img_url;
    }

    public Profile(){

    }

    public String getName(){
        return Name;
    }

    public void setName(String Name){
        this.Name = Name;
    }

    public Integer getAge(){
        return Age;
    }

    public void setAge(Integer Age){
        this.Age = Age;
    }




}
