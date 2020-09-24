package com.test.github.user.finder.framework.core.view


import android.view.View

interface BindingViewHolder<in T> : BindingView {

    fun onItemClick(view: View, item: T)

}