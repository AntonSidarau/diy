package com.example.myapplication

import android.app.Application
import android.util.Log

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val appPresenter: AppPresenter = AppPresenter()
        Log.d(TAG, "appPresenter in APP ${appPresenter.hashCode()}")
    }
}