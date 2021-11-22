# DIY (Do it yourself) - DI (service locator) of healthy person.

## Because only healthy person will be able to write the same amount of boilerplate as dagger does.

### Some notes:

1. This is variant when you can open same multiple `ComponentContainer`'s one after another.
2. It's more like proof of concept rather than another inversion of control implementation
3. KMP version TBD

### Pros:

1. Compile-time safety and even more. IDE will show red underline if there is no dependency in component.
2. No use of kapt = fast build.
3. Really easy to migrate from dagger.
4. It can be used in kotlin multiplatform projects (theoretically). TBD.
5. Flexibility, because it's just a bunch of interfaces.

### Cons:

1. Cyclic dependencies will not be detected.
2. You have to write code as much as with dagger (maybe a little bit less).
3. You have keep in mind a lot of stuff (if you want to use it properly. I guess it fits dagger, too).
4. Another di (service locator).

### What's the idea

Long story short, if you want to speed up project build, you will eventually come to solutions like:

1. Absence of `@Subcomponent` annotation.
2. Only methods with `@Provide` annotation in dagger modules.
3. Remove all `@Inject` annotations with field in your injection targets, such as Activity, Fragment, etc.
4. Provide dependencies as less as possible. It means if only one class requires some dependency - it is better to call
   constructor of this dependency manually. Smth like `return MyClass(DependencyImplementation())`
5. Do not use `@Inject` annotation in constructor. Or limit this usage.
6. Remove Hilt (Or do not come up to idea to even use it, psycho)

Eventually, your Component will be like this:

```
interface Component {
    val presenter: Presenter
    
    val analytics: Analytics
}
```

And somewhere in your Fragment dependencies will be provided through `getComponent().presenter`. Seems like service
locator, huh?

So why not to remove this annotations, kapt and do it yourself? We will need interfaces `Component`, `Dependencies`
, `Module`. Logic of Component and Module will be the same as in dagger, but as for dependencies:

1. scoped dependency will be just `val dep: Dep = Dep()` - Will be created together with it's component
2. scoped dependency via `by lazy` - Will be created once when retrieved only
3. unscoped dependency `via val dep: Dep get() = Dep()`

Instead of `@Qualifier` annotation - just name of your dependency. `val fooPresenter: Presenter`
and `val barPresenter: Presenter`. Creation of modules' implementation can be whatever you like. I've chosen variant
via `create()` method in class companion.

### Sample app

In general I don't insist in specific way to store and manage your components, so you can use what you've used before.
Af for my implementation - you can found it in `com.example.myapplication.di` and `com.example.myapplication.lifecycle` packages. 
Solution isn't perfect and originally was intended to use in kmp projects, but for now it isn't :)

Important: I'm using `ComponentContainer` in a way, that this container can have only one component.
