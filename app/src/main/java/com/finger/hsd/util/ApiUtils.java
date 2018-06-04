package com.finger.hsd.util;

public class ApiUtils {
    public static final String BASE_URL = "https://hsdvn.ga:7070/";

    public static RetrofitService getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(RetrofitService.class);
    }
}