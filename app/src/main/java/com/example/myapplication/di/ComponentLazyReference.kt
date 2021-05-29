package com.example.myapplication.di

class ComponentLazyReference<T : Any>(
    internal val componentClass: Class<T>,
    private val provideComponent: () -> T
) : RemovableComponent<T> {

    private var storedComponent: T? = null

    override fun get(): T {
        var component = storedComponent
        if (component == null) {
            storedComponent = provideComponent()
        }

        return storedComponent!!
    }

    override fun onRemove() {
        storedComponent = null
    }
}
