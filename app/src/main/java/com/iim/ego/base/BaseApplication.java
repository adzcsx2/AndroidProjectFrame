package com.iim.ego.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by Hoyn on 17/3/24.
 */

public class BaseApplication extends Application{

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContextObject(){
        return context;
    }


}
