package com.example.myapplication.di

interface RemovableComponent<T> {

    fun get(): T

    fun onRemove()
}