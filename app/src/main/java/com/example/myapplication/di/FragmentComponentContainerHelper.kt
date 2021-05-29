package com.example.myapplication.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentComponentContainerHelper : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        if (f is ComponentContainer && !f.requireActivity().isChangingConfigurations) {
            Injector.remove(f)
        }
    }
}