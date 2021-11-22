package com.example.myapplication.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapplication.di.ComponentContainer
import com.example.myapplication.di.Injector

class FragmentComponentContainerHelper : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        if (f !is ComponentContainer<*>) {
            return
        }

        if (!f.requireActivity().isChangingConfigurations) {
            Injector.remove(f)
            return
        }

        if (f.isStateSaved) {
            return
        }

        var anyParentIsRemoving = false
        var parent = f.parentFragment
        while (!anyParentIsRemoving && parent != null) {
            anyParentIsRemoving = parent.isRemoving && !parent.isStateSaved
            parent = parent.parentFragment
        }
        if (f.isRemoving || anyParentIsRemoving) {
            Injector.remove(f)
        }
    }
}
