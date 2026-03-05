package com.itsraj.funkytalk

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class FunkyTalkApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val options = FirebaseOptions.Builder()
            .setProjectId("funkytalk-317cf")
            .setApplicationId("1:665352364995:android:eda0018830a599e0431376")
            .setApiKey("AIzaSyCbuuJaW1mppWpX8pZCgiyK1JPJtt9ZnWc")
            .setStorageBucket("funkytalk-317cf.firebasestorage.app")
            .build()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this, options)
        }
    }
}
