package com.prometteur.sathiya.model;

public class UserDataObject extends ListObject {
        private String userPhoto,userName,location,matriId;

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMatriId() {
        return matriId;
    }

    public void setMatriId(String matriId) {
        this.matriId = matriId;
    }

    @Override
        public int getType() {
            return TYPE_DATE;
        }
    }