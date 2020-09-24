package com.test.github.user.finder.framework.core.base

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.test.github.user.finder.framework.core.owner.ViewDataBindingOwner
import com.test.github.user.finder.framework.core.owner.ViewModelOwner
import com.test.github.user.finder.framework.core.view.BindingViewHolder
import com.test.github.user.finder.framework.BR

abstract class BaseViewHolder<T>(val context: Context, view: View) : RecyclerView.ViewHolder(view) {

    init {
        if (this is ViewDataBindingOwner<*>) {
            setViewBinding(view)
            if (this is ViewModelOwner<*>) {
                binding.setVariable(BR.vm, viewModel)
                binding.executePendingBindings()
            }
            if (this is BindingViewHolder<*>) {
                binding.setVariable(BR.view, this)
            }
        }
    }

    abstract fun bindData(data: T)
}
