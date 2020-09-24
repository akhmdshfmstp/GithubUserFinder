package com.test.github.user.finder.di.component

import org.koin.core.module.Module
import com.test.github.user.finder.datamodule.module.repository.repositoryModule

val repositoryComponent: List<Module> = listOf(
    repositoryModule
)