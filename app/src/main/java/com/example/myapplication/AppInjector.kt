package com.example.myapplication

class AppInjector private constructor(val component: AppComponent) {

    companion object {

        private lateinit var instance: AppInjector

        fun newInstance(appComponent: AppComponent) {
            instance = AppInjector(appComponent)
        }

        fun getInstance(): AppInjector = instance
    }
}

fun getAppComponent() = AppInjector.getInstance().component