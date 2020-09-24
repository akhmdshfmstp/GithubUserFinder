package com.test.github.user.finder.presentation.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.test.github.user.finder.R
import com.test.github.user.finder.datamodule.model.dto.user.UserDto
import com.test.github.user.finder.framework.core.base.BasePagedListAdapter
import com.test.github.user.finder.framework.core.base.BaseViewHolder
import com.test.github.user.finder.framework.design.NetworkStateItemPagingViewHolder

class UserAdapter(
    private val onItemClicked: (data: UserDto.Items) -> Unit,
    retryCallback: () -> Unit
) :
    BasePagedListAdapter<UserDto.Items>(ITEM_COMPARATOR, retryCallback) {
    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<UserDto.Items>() {
            override fun areItemsTheSame(
                oldItem: UserDto.Items,
                newItem: UserDto.Items
            ) : Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: UserDto.Items,
                newItem: UserDto.Items
            ): Boolean = oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_user -> (holder as UserItemViewHolder).bindData(
                getItem(position)!!
            )
            R.layout.network_state_item   -> (holder as NetworkStateItemPagingViewHolder).bindData(
                getNetworkState()!!
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.item_user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            R.layout.item_user -> UserItemViewHolder.create(
                parent,
                onItemClicked
            )
            R.layout.network_state_item -> NetworkStateItemPagingViewHolder.create(
                parent,
                retryCallback
            )
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

}