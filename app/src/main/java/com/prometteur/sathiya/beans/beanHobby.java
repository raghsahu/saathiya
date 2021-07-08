package com.prometteur.sathiya.beans;

import java.io.Serializable;

/**
 * Created by Ravi on 10/24/2016.
 */
public class beanHobby implements Serializable {
    String hobby_id,hobby_name;

    private boolean isSelected;

    public beanHobby(String hobby_id, String hobby_name) {
        this.hobby_id = hobby_id;
        this.hobby_name = hobby_name;

    }
    public beanHobby(String hobby_id, String hobby_name, boolean isSelected) {
        this.hobby_id = hobby_id;
        this.hobby_name = hobby_name;
        this.isSelected=isSelected;
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
}
