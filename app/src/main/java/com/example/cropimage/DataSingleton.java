package com.example.cropimage;

/**
 * Created by duchieu on 5/18/17.
 */

public class DataSingleton {
    private static DataSingleton instance = null;
    public String uriImage ;
    public Boolean crop;
    public int deviceWidth;
    public int deviceHeight;
    private DataSingleton() {
        // Exists only to defeat instantiation.
        uriImage = "";
    }
    public static DataSingleton getInstance() {
        if (instance == null) {
            instance = new DataSingleton();
        }
        return instance;
    }
}
