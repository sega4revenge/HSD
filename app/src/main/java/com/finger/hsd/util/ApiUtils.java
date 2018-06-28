package com.finger.hsd.util;

public class ApiUtils {
    public static final String BASE_URL = "https://hansudung.com/";
//public static final String BASE_URL = "http://192.168.1.56:7070/";
//public static final String BASE_URL = "http://45.77.36.109:7070/";
    public static RetrofitService getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(RetrofitService.class);
    }
}
