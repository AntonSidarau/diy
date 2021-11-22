package com.example.myapplication.di

import android.app.Application
import com.example.myapplication.lifecycle.ActivityComponentContainerHelper
import java.util.*

object Injector {

    private val lazyReferenceStore: MutableMap<Class<*>, ComponentWrapper<*>> = linkedMapOf()
    private val relationStore: MutableMap<Class<*>, MutableSet<Any>> = WeakHashMap()

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
        unregisterContainer(container)

        val componentClass = container.componentClass
        val relatedContainers = relationStore[componentClass]
        if (relatedContainers == null || relatedContainers.isEmpty()) {
            lazyReferenceStore[componentClass]?.onRemove()
        }
    }

    fun <T : DiComponent> ComponentContainer<T>.getComponent(): T {
        registerContainer(this)

        return Injector[componentClass]
    }

    /**
     * Use only in InjectionInitializer like classes
     */
    fun <T : DiComponent> getComponentForCreation(componentClass: Class<T>): T {
        return this[componentClass]
    }

    private fun <T : DiComponent> unregisterContainer(componentContainer: ComponentContainer<T>) {
        val relatedContainers = relationStore[componentContainer.componentClass]
        relatedContainers?.remove(componentContainer)
    }

    private fun <T : DiComponent> registerContainer(componentContainer: ComponentContainer<T>) {
        val componentClass = componentContainer.componentClass
        val relatedContainers = relationStore[componentClass]
        if (relatedContainers == null) {
            relationStore[componentClass] = mutableSetOf(componentContainer)
        } else {
            relatedContainers.add(componentContainer)
        }
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
