package com.test.github.user.finder.framework.core.base


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.multidex.MultiDexApplication

abstract class BaseMultidexApplication :
    MultiDexApplication(),
    LifecycleOwner {

    internal val mLifecycleRegistry = LifecycleRegistry(this)

    init {
        mLifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    override fun getLifecycle(): LifecycleRegistry {
        return mLifecycleRegistry
    }

    override fun onCreate() {
        super.onCreate()
        mLifecycleRegistry.currentState = Lifecycle.State.STARTED
    }
}