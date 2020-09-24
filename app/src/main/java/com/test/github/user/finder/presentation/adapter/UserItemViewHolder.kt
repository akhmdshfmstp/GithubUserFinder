package com.test.github.user.finder.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.github.user.finder.R
import com.test.github.user.finder.databinding.ItemUserBinding
import com.test.github.user.finder.datamodule.model.dto.user.UserDto
import com.test.github.user.finder.framework.core.base.BaseViewHolder
import com.test.github.user.finder.framework.core.owner.ViewDataBindingOwner
import com.test.github.user.finder.framework.extention.loadImage
import com.test.github.user.finder.presentation.view.UserItemView
import com.test.github.user.finder.presentation.viewmodel.UserItemViewModel

class UserItemViewHolder(
    context: Context, view: View,
    private val onItemClicked: (data: UserDto.Items) -> Unit
) : UserItemView,
    BaseViewHolder<UserDto.Items>(context, view),
    ViewDataBindingOwner<ItemUserBinding> {

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (data: UserDto.Items) -> Unit
        ): UserItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_user, parent, false
            )
            return UserItemViewHolder(
                parent.context,
                view,
                onItemClicked
            )
        }
    }

    override lateinit var binding: ItemUserBinding
    private var viewModel: UserItemViewModel? = null
    private var item = UserDto.Items()

    init {
        binding.vm = UserItemViewModel()
        binding.view = this
        viewModel = binding.vm
    }

    override fun bindData(data: UserDto.Items) {
        item = data
        binding.textUser.text = data.login
        binding.avatar.loadImage(data.avatar_url)
    }

    override fun onItemClicked(view: View) {
        onItemClicked(item)
    }

}