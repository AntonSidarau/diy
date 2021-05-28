package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

internal const val TAG = "TEST_DI"

class AppPresenter
class ActivityPresenter
class MainFragmentPresenter
class FragmentContainerPresenter
class ChildPresenter
class SecondChildPresenter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_id, MainFragment::class.java, null)
                .commit()
        }

        val appPresenter: AppPresenter = AppPresenter()
        val activityPresenter: ActivityPresenter = ActivityPresenter()
        Log.d(TAG, "AppPresenter - @${appPresenter.hashCode()}")
        Log.d(TAG, "ActivityPresenter - @${activityPresenter.hashCode()}")
    }
}

class MainFragment : Fragment(R.layout.main_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvMain: TextView = view.findViewById(R.id.tv_main)
        val btnToContainer: Button = view.findViewById(R.id.btn_to_container)

        tvMain.text = "MainFragment@${this.hashCode()}"

        btnToContainer.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_id, ContainerFragment::class.java, null)
                .addToBackStack("Container")
                .commit()
        }

        val mainPresenter: MainFragmentPresenter = MainFragmentPresenter()
        val activityPresenter: ActivityPresenter = ActivityPresenter()
        Log.d(TAG, "MainFragmentPresenter - @${mainPresenter.hashCode()}")
        Log.d(TAG, "ActivityPresenter in MainFragment - @${activityPresenter.hashCode()}")
    }
}

class ContainerFragment : Fragment(R.layout.container_fragment) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (childFragmentManager.fragments[0].tag == "C2") {
                    childFragmentManager.popBackStack()
                } else {
                    parentFragmentManager.popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.title = "ContainerFragment@${this.hashCode()}"

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_id, ChildFragment::class.java, null)
                .commit()
        }

        val presenter: FragmentContainerPresenter = FragmentContainerPresenter()
        Log.d(TAG, "FragmentContainerPresenter - @${presenter.hashCode()}")
    }
}

class ChildFragment : Fragment(R.layout.child_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvChild: TextView = view.findViewById(R.id.tv_child)
        val btnToSecondChild: Button = view.findViewById(R.id.btn_to_second_child)

        tvChild.text = "ChildFragment@${this.hashCode()}"
        btnToSecondChild.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_id, SecondChildFragment::class.java, null, "C2")
                .addToBackStack("C2")
                .commit()
        }

        val presenter: ChildPresenter = ChildPresenter()
        val containerPresenter: FragmentContainerPresenter = FragmentContainerPresenter()
        val activityPresenter: ActivityPresenter = ActivityPresenter()
        Log.d(TAG, "ChildPresenter - @${presenter.hashCode()}")
        Log.d(TAG, "FragmentContainerPresenter in Child - @${containerPresenter.hashCode()}")
        Log.d(TAG, "ActivityPresenter in Child - @${activityPresenter.hashCode()}")
    }
}

class SecondChildFragment : Fragment(R.layout.second_child_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvChild: TextView = view.findViewById(R.id.tv_second_child)

        tvChild.text = "SecondChildFragment@${this.hashCode()}"

        val presenter: SecondChildPresenter = SecondChildPresenter()
        val containerPresenter: FragmentContainerPresenter = FragmentContainerPresenter()
        Log.d(TAG, "SecondChildPresenter - @${presenter.hashCode()}")
        Log.d(TAG, "FragmentContainerPresenter in SecondChild - @${containerPresenter.hashCode()}")
    }
}