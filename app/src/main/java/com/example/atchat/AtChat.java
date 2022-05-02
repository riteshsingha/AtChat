package com.example.atchat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class AtChat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
