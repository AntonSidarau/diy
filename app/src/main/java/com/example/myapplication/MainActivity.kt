package com.example.myapplication

import android.app.Application
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
import com.example.myapplication.di.ComponentContainer
import com.example.myapplication.di.DiComponent
import com.example.myapplication.di.Injector.getComponent
import com.example.myapplication.di.lazyUnsafe

internal const val TAG = "TEST_DI"

class AppPresenter
class ActivityPresenter
class MainFragmentPresenter
class FragmentContainerPresenter
class ChildPresenter
class SecondChildPresenter

// region App scope
interface AppComponent : AppModule {

    override val appPresenter: AppPresenter

    companion object {

        fun create(app: Application): AppComponent {
            val appModule = AppModule.create(app)
            return object : AppComponent,
                AppModule by appModule {
            }
        }
    }
}

interface AppModule {

    val appPresenter: AppPresenter
    val appContext: Context
    val application: Application

    companion object {

        fun create(app: Application): AppModule {
            return object : AppModule {

                override val appPresenter: AppPresenter by lazyUnsafe { AppPresenter() }
                override val appContext: Context = app
                override val application: Application = app
            }
        }
    }
}
// endregion

// region Root (Activity) scope
abstract class RootComponent : DiComponent,
    AppModule,
    FragmentContainerDependencies,
    MainFragmentDependencies {

    companion object {

        fun create(): RootComponent {
            return object : RootComponent(),
                AppModule by getAppComponent() {

                override val activityPresenter: ActivityPresenter by lazyUnsafe { ActivityPresenter() }
            }
        }
    }

    abstract override val activityPresenter: ActivityPresenter

    fun createMainFragmentComponent(): MainFragmentComponent {
        return MainFragmentComponent.create(this)
    }

    fun createFragmentContainerComponent(): FragmentContainerComponent {
        return FragmentContainerComponent.create(this)
    }
}
// endregion

// region MainFragment scope
interface MainFragmentDependencies {

    val activityPresenter: ActivityPresenter
}

interface MainFragmentComponent : DiComponent, MainFragmentDependencies {

    val mainFragmentPresenter: MainFragmentPresenter

    companion object {

        fun create(dependencies: MainFragmentDependencies): MainFragmentComponent {
            return object : MainFragmentComponent,
                MainFragmentDependencies by dependencies {

                override val mainFragmentPresenter: MainFragmentPresenter by lazyUnsafe {
                    MainFragmentPresenter()
                }
            }
        }
    }
}
// endregion

// region FragmentContainer scope
interface FragmentContainerDependencies {

    val activityPresenter: ActivityPresenter
}

interface FragmentContainerModule {

    val fragmentContainerPresenter: FragmentContainerPresenter

    companion object {

        fun create(): FragmentContainerModule {
            return object : FragmentContainerModule {
                override val fragmentContainerPresenter: FragmentContainerPresenter by lazyUnsafe {
                    FragmentContainerPresenter()
                }
            }
        }
    }
}

interface FragmentContainerComponent :
    DiComponent,
    FragmentContainerModule,
    ChildDependencies,
    SecondChildDependencies {

    companion object {

        fun create(deps: FragmentContainerDependencies): FragmentContainerComponent {
            val module = FragmentContainerModule.create()
            return object : FragmentContainerComponent,
                FragmentContainerModule by module,
                FragmentContainerDependencies by deps {
            }
        }
    }

    override val fragmentContainerPresenter: FragmentContainerPresenter

    fun createChildComponent(): ChildComponent {
        return ChildComponent.create(this)
    }

    fun createSecondChildComponent(): SecondChildComponent {
        return SecondChildComponent.create(this)
    }
}
// endregion

// region ChildFragment scope
interface ChildDependencies {

    val fragmentContainerPresenter: FragmentContainerPresenter

    val activityPresenter: ActivityPresenter
}

interface ChildComponent : DiComponent, ChildDependencies {

    companion object {

        fun create(deps: ChildDependencies): ChildComponent {
            return object : ChildComponent, ChildDependencies by deps {

                override val childPresenter: ChildPresenter by lazyUnsafe { ChildPresenter() }
            }
        }
    }

    val childPresenter: ChildPresenter
}
// endregion

// region SecondChildFragment scope
interface SecondChildDependencies {

    val fragmentContainerPresenter: FragmentContainerPresenter
}

interface SecondChildComponent : DiComponent, SecondChildDependencies {

    companion object {

        fun create(deps: SecondChildDependencies): SecondChildComponent {
            return object : SecondChildComponent, SecondChildDependencies by deps {

                override val secondChildPresenter: SecondChildPresenter by lazyUnsafe {
                    SecondChildPresenter()
                }
            }
        }
    }

    val secondChildPresenter: SecondChildPresenter
}
// endregion


// ========== Example Screens ============

class MainActivity : AppCompatActivity(), ComponentContainer<RootComponent> {

    override val componentClass get() = RootComponent::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_id, MainFragment::class.java, null)
                .commit()
        }

        val component: RootComponent = getComponent()
        val appPresenter: AppPresenter = component.appPresenter
        val activityPresenter: ActivityPresenter = component.activityPresenter
        Log.d(TAG, "AppPresenter - @${appPresenter.hashCode()}")
        Log.d(TAG, "ActivityPresenter - @${activityPresenter.hashCode()}")
    }
}

class MainFragment : Fragment(R.layout.main_fragment), ComponentContainer<MainFragmentComponent> {

    override val componentClass get() = MainFragmentComponent::class.java

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

        val component: MainFragmentComponent = getComponent()
        val mainPresenter: MainFragmentPresenter = component.mainFragmentPresenter
        val activityPresenter: ActivityPresenter = component.activityPresenter
        Log.d(TAG, "MainFragmentPresenter - @${mainPresenter.hashCode()}")
        Log.d(TAG, "ActivityPresenter in MainFragment - @${activityPresenter.hashCode()}")
    }
}

class ContainerFragment : Fragment(R.layout.container_fragment), ComponentContainer<FragmentContainerComponent> {

    override val componentClass get() = FragmentContainerComponent::class.java

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

        val component: FragmentContainerComponent = getComponent()
        val presenter: FragmentContainerPresenter = component.fragmentContainerPresenter
        Log.d(TAG, "FragmentContainerPresenter - @${presenter.hashCode()}")
    }
}

class ChildFragment : Fragment(R.layout.child_fragment), ComponentContainer<ChildComponent> {

    override val componentClass get() = ChildComponent::class.java

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

        val component: ChildComponent = getComponent()
        val presenter: ChildPresenter = component.childPresenter
        val containerPresenter: FragmentContainerPresenter = component.fragmentContainerPresenter
        val activityPresenter: ActivityPresenter = component.activityPresenter
        Log.d(TAG, "ChildPresenter - @${presenter.hashCode()}")
        Log.d(TAG, "FragmentContainerPresenter in Child - @${containerPresenter.hashCode()}")
        Log.d(TAG, "ActivityPresenter in Child - @${activityPresenter.hashCode()}")
    }
}

class SecondChildFragment : Fragment(R.layout.second_child_fragment), ComponentContainer<SecondChildComponent> {

    override val componentClass get() = SecondChildComponent::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvChild: TextView = view.findViewById(R.id.tv_second_child)

        tvChild.text = "SecondChildFragment@${this.hashCode()}"

        val component: SecondChildComponent = getComponent()
        val presenter: SecondChildPresenter = component.secondChildPresenter
        val containerPresenter: FragmentContainerPresenter = component.fragmentContainerPresenter
        Log.d(TAG, "SecondChildPresenter - @${presenter.hashCode()}")
        Log.d(TAG, "FragmentContainerPresenter in SecondChild - @${containerPresenter.hashCode()}")
    }
}
