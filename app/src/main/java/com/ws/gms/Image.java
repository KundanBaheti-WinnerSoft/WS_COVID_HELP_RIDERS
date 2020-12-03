package com.ws.gms;

import java.io.Serializable;

public class Image implements Serializable {

    int imageId;
    private String name;
    private String small, medium, large;

    public Image() {
    }

    public Image(int imageId, String name, String small, String medium, String large) {
        this.imageId = imageId;
        this.name = name;
        this.small = small;
        this.medium = medium;
        this.large = large;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }
}
