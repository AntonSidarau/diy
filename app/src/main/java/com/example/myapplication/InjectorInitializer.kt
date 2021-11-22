package com.example.myapplication

import android.app.Application
import com.example.myapplication.di.ComponentWrapper
import com.example.myapplication.di.Injector
import com.example.myapplication.di.Injector.getComponent

object InjectorInitializer {

    fun init(application: Application) {
        AppInjector.newInstance(appComponent(application))

        Injector.init(
            application,
            listOf(
                ComponentWrapper(RootComponent::class.java) {
                    rootComponent()
                },
                ComponentWrapper(MainFragmentComponent::class.java) {
                    getComponent<RootComponent>()
                        .createMainFragmentComponent()
                },
                ComponentWrapper(FragmentContainerComponent::class.java) {
                    getComponent<RootComponent>()
                        .createFragmentContainerComponent()
                },
                ComponentWrapper(ChildComponent::class.java) {
                    getComponent<FragmentContainerComponent>()
                        .createChildComponent()
                },
                ComponentWrapper(SecondChildComponent::class.java) {
                    getComponent<FragmentContainerComponent>()
                        .createSecondChildComponent()
                }
            )
        )
    }
}
