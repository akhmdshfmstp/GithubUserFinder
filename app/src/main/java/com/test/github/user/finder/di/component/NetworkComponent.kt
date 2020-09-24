package com.test.github.user.finder.di.component

import com.test.github.user.finder.di.module.network.networkModule
import org.koin.core.module.Module

val networkComponent: List<Module> = listOf(
    networkModule
)