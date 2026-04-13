package com.example.boardinghousefinder.models;

import android.net.Uri;

public class PropertyUnit {
    private String unitName;
    private String price;
    private String deposit;
    private String description;
    private Uri photoUri;          // local URI before upload
    private String imageUrl;       // server URL after upload

    public PropertyUnit(String unitName, String price, String deposit,
                        String description, Uri photoUri) {
        this.unitName    = unitName;
        this.price       = price;
        this.deposit     = deposit;
        this.description = description;
        this.photoUri    = photoUri;
    }

    public String getUnitName()    { return unitName; }
    public String getPrice()       { return price; }
    public String getDeposit()     { return deposit; }
    public String getDescription() { return description; }
    public Uri    getPhotoUri()    { return photoUri; }
    public String getImageUrl()    { return imageUrl; }
    public void   setImageUrl(String url) { this.imageUrl = url; }
}