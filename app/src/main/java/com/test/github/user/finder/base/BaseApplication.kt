package com.test.github.user.finder.base

import android.content.Context
import androidx.multidex.MultiDex
import com.ashokvarma.gander.Gander
import com.ashokvarma.gander.imdb.GanderIMDB
import com.test.github.user.finder.di.component.networkComponent
import com.test.github.user.finder.di.component.repositoryComponent
import com.test.github.user.finder.di.component.viewModelComponent
import com.test.github.user.finder.framework.core.base.BaseMultidexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BaseApplication : BaseMultidexApplication() {

    companion object {
        var appContext: Context? = null
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this

        setupGander()
        setupKoin()
    }

    private fun setupGander() {
        Gander.setGanderStorage(GanderIMDB.getInstance())
    }

    private fun setupKoin() {
        // start Koin context
        startKoin {
            androidContext(this@BaseApplication)
            androidLogger(Level.DEBUG)
            modules(networkComponent)
            modules(repositoryComponent)
            modules(viewModelComponent)
        }
    }

}