package com.example.myapplication.di

interface ComponentContainer<T : DiComponent> {

    val componentClass: Class<T>
}

