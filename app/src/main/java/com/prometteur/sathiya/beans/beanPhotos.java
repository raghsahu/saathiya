package com.prometteur.sathiya.beans;

/**
 * Created by Ravi on 10/24/2016.
 */
public class beanPhotos
{
    String id,imageURL,approveStatus;

    public beanPhotos(String id, String imageURL, String approveStatus) {
        this.id = id;
        this.imageURL = imageURL;
        this.approveStatus = approveStatus;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }
}
