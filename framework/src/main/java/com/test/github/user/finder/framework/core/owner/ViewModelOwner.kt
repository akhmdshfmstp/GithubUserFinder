package com.test.github.user.finder.framework.core.owner

import com.test.github.user.finder.framework.core.base.BaseViewModel

interface ViewModelOwner<T : BaseViewModel> {
    val viewModel: T
}