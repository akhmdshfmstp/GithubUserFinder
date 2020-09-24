package com.test.github.user.finder.datamodule.module.repository

import com.test.github.user.finder.datamodule.repository.user.UserRepository
import com.test.github.user.finder.datamodule.repository.user.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<UserRepository> { UserRepositoryImpl(userService = get()) }

}