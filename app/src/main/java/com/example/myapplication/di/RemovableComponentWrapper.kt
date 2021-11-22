package com.example.myapplication.di

interface RemovableComponentWrapper<T> {

    fun get(): T

    fun onRemove()
}
