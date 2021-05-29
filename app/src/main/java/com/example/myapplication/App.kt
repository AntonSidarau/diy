package com.example.myapplication

import android.app.Application
import android.util.Log
import com.example.myapplication.di.InjectorInitializer

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        InjectorInitializer.init(this)

        val appPresenter: AppPresenter = getAppComponent().appPresenter
        Log.d(TAG, "appPresenter in APP ${appPresenter.hashCode()}")
    }
}