package com.example.myapplication.di

import android.app.Application
import com.example.myapplication.*
import com.example.myapplication.di.Injector.getComponent

object InjectorInitializer {

    fun init(application: Application) {
        AppInjector.newInstance(appComponent(application))
        Injector.init(
            application, listOf(
                ComponentLazyReference(RootComponent::class.java) {
                    rootComponent()
                },
                ComponentLazyReference(MainFragmentComponent::class.java) {
                    getComponent<RootComponent>()
                        .createMainFragmentComponent()
                },
                ComponentLazyReference(FragmentContainerComponent::class.java) {
                    getComponent<RootComponent>()
                        .createFragmentContainerComponent()
                },
                ComponentLazyReference(ChildComponent::class.java) {
                    getComponent<FragmentContainerComponent>()
                        .createChildComponent()
                },
                ComponentLazyReference(SecondChildComponent::class.java) {
                    getComponent<FragmentContainerComponent>()
                        .createSecondChildComponent()
                }
            )
        )
    }
}