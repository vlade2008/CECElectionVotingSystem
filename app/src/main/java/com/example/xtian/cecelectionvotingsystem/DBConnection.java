package com.example.xtian.cecelectionvotingsystem;

/**
 * Created by Xtian on 11/12/2015.
 */
public class DBConnection {

    String url = "jdbc:mysql://192.168.254.102:3306/finals?user=admin&password=admin";
    public static String id;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
