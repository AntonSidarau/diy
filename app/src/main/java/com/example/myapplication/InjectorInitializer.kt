package com.example.myapplication

import android.app.Application
import com.example.myapplication.di.ComponentWrapper
import com.example.myapplication.di.DiComponent
import com.example.myapplication.di.Injector

object InjectorInitializer {

    fun init(application: Application) {
        AppInjector.newInstance(AppComponent.create(application))

        Injector.init(
            application,
            listOf(
                ComponentWrapper(RootComponent::class.java) {
                    RootComponent.create()
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

    private inline fun <reified T : DiComponent> InjectorInitializer.getComponent(): T {
        return Injector.getComponentForCreation(T::class.java)
    }
}
