package com.test.github.user.finder.di.component

import com.test.github.user.finder.di.module.viewmodel.viewModelModule
import org.koin.core.module.Module

val viewModelComponent: List<Module> = listOf(
    viewModelModule
)