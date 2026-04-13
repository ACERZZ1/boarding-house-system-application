package com.example.boardinghousefinder.models;

import java.util.ArrayList;

public class BoardingHouse {

    private int id;
    private String title, propertyType, price, deposit, description;
    private String rules, services, maxGuest, bedrooms, beds, bathrooms;
    private String streetAddress, city, province, zipCode, country;
    private String firstName, lastName, email, phone;
    private String coverImageUrl;
    private ArrayList<String> galleryImageUrls;
    private double latitude;
    private double longitude;
    private int isReserved; // 0 = Available, 1 = Reserved

    // Old constructor — keeps sample/static data compiling
    public BoardingHouse(String title, String price, String description, int imageRes) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.galleryImageUrls = new ArrayList<>();
    }

    // Full constructor — used when loading from server
    public BoardingHouse(int id, String title, String propertyType,
                         String price, String deposit, String description, String rules,
                         String services, String maxGuest, String bedrooms,
                         String beds, String bathrooms,
                         String streetAddress, String city, String province,
                         String zipCode, String country,
                         String firstName, String lastName, String email, String phone,
                         double latitude, double longitude,
                         String coverImageUrl, ArrayList<String> galleryImageUrls,
                         int isReserved) {
        this.id = id;
        this.title = title;
        this.propertyType = propertyType;
        this.price = price;
        this.deposit = deposit;
        this.description = description;
        this.rules = rules;
        this.services = services;
        this.maxGuest = maxGuest;
        this.bedrooms = bedrooms;
        this.beds = beds;
        this.bathrooms = bathrooms;
        this.streetAddress = streetAddress;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
        this.country = country;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.coverImageUrl = coverImageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.galleryImageUrls = galleryImageUrls != null ? galleryImageUrls : new ArrayList<>();
        this.isReserved = isReserved;
    }

    public int getId()                          { return id; }
    public String getTitle()                    { return title; }
    public String getName()                     { return title; } // alias for old adapters
    public String getPropertyType()             { return propertyType; }
    public String getPrice()                    { return price; }
    public String getDeposit()                  { return deposit; }
    public String getDescription()              { return description; }
    public String getRules()                    { return rules; }
    public String getServices()                 { return services; }
    public String getMaxGuest()                 { return maxGuest; }
    public String getBedrooms()                 { return bedrooms; }
    public String getBeds()                     { return beds; }
    public String getBathrooms()                { return bathrooms; }
    public String getStreetAddress()            { return streetAddress; }
    public String getCity()                     { return city; }
    public String getProvince()                 { return province; }
    public String getZipCode()                  { return zipCode; }
    public String getCountry()                  { return country; }
    public String getFirstName()                { return firstName; }
    public String getLastName()                 { return lastName; }
    public String getEmail()                    { return email; }
    public String getPhone()                    { return phone; }
    public String getCoverImageUrl()            { return coverImageUrl; }
    public int getImage()                       { return 0; } // alias for old adapters
    public String getFullAddress()              { return streetAddress + ", " + city + ", " + province; }
    public double getLatitude()                 { return latitude; }
    public double getLongitude()                { return longitude; }
    public ArrayList<String> getGalleryImageUrls() { return galleryImageUrls; }
    public int getIsReserved()                  { return isReserved; }

}
