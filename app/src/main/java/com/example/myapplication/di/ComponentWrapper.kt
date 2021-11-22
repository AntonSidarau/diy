package com.example.myapplication.di

class ComponentWrapper<T : DiComponent>(
    internal val componentClass: Class<T>,
    private val provideComponent: () -> T
) : RemovableComponentWrapper<T> {

    private var storedComponent: T? = null

    override fun get(): T {
        var component = storedComponent
        if (component == null) {
            storedComponent = provideComponent()
        }

        return storedComponent!!
    }

    override fun onRemove() {
        storedComponent?.onCleared()
        storedComponent = null
    }
}
