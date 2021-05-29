package com.example.myapplication.di

import android.app.Application

object Injector {

    private val lazyReferenceStore: MutableMap<Class<*>, ComponentLazyReference<*>> = linkedMapOf()

    fun init(
        application: Application,
        componentLazyReferenceList: List<ComponentLazyReference<*>>
    ) {
        for (componentLazyReference in componentLazyReferenceList) {
            lazyReferenceStore[componentLazyReference.componentClass] = componentLazyReference
        }
        application.registerActivityLifecycleCallbacks(ActivityComponentContainerHelper())
    }

    fun remove(container: ComponentContainer) {
        lazyReferenceStore[container.componentClass]?.onRemove()
    }

    fun <T : Any> getComponent(componentClass: Class<*>): T {
        return this[componentClass]
    }

    fun <T : Any> ComponentContainer.getComponent(): T {
        return getComponent(this.componentClass)
    }

    inline fun <reified T : Any> InjectorInitializer.getComponent(): T {
        return getComponent(T::class.java)
    }

    private operator fun <T : Any> get(componentClass: Class<*>): T {
        val componentLazyReference = lazyReferenceStore[componentClass]
            ?: throw IllegalStateException(
                "Provide ComponentLazyReference for ${componentClass.name} in InjectorInitializer"
            )

        @Suppress("UNCHECKED_CAST")
        return componentLazyReference.get() as T
    }
}