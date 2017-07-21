package com.example.cropimage;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by duchieu on 5/18/17.
 */

public class CropApplication extends Application{
    public CropApplication(){
        super();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        DataSingleton.getInstance().deviceWidth = metrics.widthPixels;
        DataSingleton.getInstance().deviceHeight = metrics.heightPixels;
    }
}
