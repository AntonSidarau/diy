package com.example.myapplication.di

import android.app.Application
import com.example.myapplication.InjectorInitializer
import com.example.myapplication.lifecycle.ActivityComponentContainerHelper

object Injector {

    private val lazyReferenceStore: MutableMap<Class<*>, ComponentWrapper<*>> = linkedMapOf()

    fun init(
        application: Application,
        componentWrappers: List<ComponentWrapper<*>>
    ) {
        for (componentLazyReference in componentWrappers) {
            lazyReferenceStore[componentLazyReference.componentClass] = componentLazyReference
        }
        application.registerActivityLifecycleCallbacks(ActivityComponentContainerHelper())
    }

    fun remove(container: ComponentContainer<*>) {
        lazyReferenceStore[container.componentClass]?.onRemove()
    }

    fun <T : DiComponent> getComponent(componentClass: Class<T>): T {
        return this[componentClass]
    }

    fun <T : DiComponent> ComponentContainer<T>.getComponent(): T {
        return getComponent(this.componentClass)
    }

    inline fun <reified T : DiComponent> InjectorInitializer.getComponent(): T {
        return getComponent(T::class.java)
    }

    private operator fun <T : DiComponent> get(componentClass: Class<T>): T {
        val componentLazyReference = lazyReferenceStore[componentClass]
            ?: throw IllegalStateException(
                "Provide ComponentLazyReference for ${componentClass.name} in InjectorInitializer"
            )

        @Suppress("UNCHECKED_CAST")
        return componentLazyReference.get() as T
    }
}
