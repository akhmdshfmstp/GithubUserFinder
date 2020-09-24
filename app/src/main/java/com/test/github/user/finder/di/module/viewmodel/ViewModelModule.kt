package com.test.github.user.finder.di.module.viewmodel

import com.test.github.user.finder.presentation.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainViewModel(
            get()
        )
    }

}