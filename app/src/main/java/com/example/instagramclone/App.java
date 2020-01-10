package com.example.instagramclone;

import android.app.Application;
import com.parse.Parse;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("iP0NxfByAs7z5blm6TWxFlYTEF10WEnfTafkATgO")
                // if defined
                .clientKey("k5I0lD5f1R2jVSWkPzjtV4ggzQtQDbYmTgwf40jA")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
