package com.prometteur.sathiya.beans;

import java.io.Serializable;

/**
 * Created by Ravi on 10/24/2016.
 */
public class beanHobbyImage implements Serializable {
    String hobby_id,hobby_name,matri_id,intrest_id,photo,name,location,userPhoto,isLiked,approveStatus;

    private boolean isSelected;

    public beanHobbyImage(String hobby_id, String hobby_name) {
        this.hobby_id = hobby_id;
        this.hobby_name = hobby_name;

    }
    public beanHobbyImage(String hobby_id, String hobby_name,String matri_id,String intrest_id,String photo,String approveStatus) {
        this.hobby_id = hobby_id;
        this.hobby_name = hobby_name;
        this.matri_id=matri_id;
        this.intrest_id=intrest_id;
        this.photo=photo;
        this.approveStatus=approveStatus;
    }
    public beanHobbyImage(String hobby_id, String hobby_name,String matri_id,String intrest_id,String photo,String name,String city,String userPhoto,String isLiked) {
        this.hobby_id = hobby_id;
        this.hobby_name = hobby_name;
        this.matri_id=matri_id;
        this.intrest_id=intrest_id;
        this.photo=photo;
        this.name=name;
        this.location=city;
        this.userPhoto=userPhoto;
        this.isLiked=isLiked;
    }

    public String getHobby_id() {
        return hobby_id;
    }

    public void setHobby_id(String hobby_id) {
        this.hobby_id = hobby_id;
    }

    public String getHobby_name() {
        return hobby_name;
    }

    public void setHobby_name(String hobby_name) {
        this.hobby_name = hobby_name;
    }

    public String getMatri_id() {
        return matri_id;
    }

    public void setMatri_id(String matri_id) {
        this.matri_id = matri_id;
    }

    public String getIntrest_id() {
        return intrest_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIntrest_id(String intrest_id) {
        this.intrest_id = intrest_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(String isLiked) {
        this.isLiked = isLiked;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }
}
