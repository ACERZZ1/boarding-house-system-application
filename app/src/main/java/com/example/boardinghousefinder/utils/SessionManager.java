package com.example.boardinghousefinder.utils;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    // CHANGE YOUR IP HERE ONLY:
    public static final String BASE_URL = "http://192.168.254.104/casptone/";

    private static boolean isLoggedIn = false;
    private static String userRole    = "Guest"; // Guest, Boarder, Owner

    // Profile fields
    private static int    userId   = -1;
    private static String username = "";
    private static String email    = "";
    private static String phone    = "";
    private static String address  = "";

    private static List<String> ownerListings = new ArrayList<>();

    // ─── Auth ────────────────────────────────────────────────────────────────

    public static boolean isLoggedIn()            { return isLoggedIn; }
    public static void setLoggedIn(boolean value) { isLoggedIn = value; }

    public static String getUserRole()            { return userRole; }
    public static void setUserRole(String role)   { userRole = role; }

    public static void logout() {
        isLoggedIn = false;
        userRole   = "Guest";
        userId     = -1;
        username   = "";
        email      = "";
        phone      = "";
        address    = "";
        ownerListings.clear();
    }

    // ─── Profile ─────────────────────────────────────────────────────────────

    public static int    getUserId()              { return userId; }
    public static void   setUserId(int id)        { userId = id; }

    public static String getUsername()            { return username; }
    public static void   setUsername(String name) { username = name; }

    public static String getEmail()               { return email; }
    public static void   setEmail(String e)       { email = e; }

    public static String getPhone()               { return phone; }
    public static void   setPhone(String p)       { phone = p; }

    public static String getAddress()             { return address; }
    public static void   setAddress(String a)     { address = a; }

    public static List<String> getOwnerListings() { return ownerListings; }
    public static void addListing(String title, String location, String price) {
        ownerListings.add(title + "||" + location + "||" + price);
    }
}
