package com.test.github.user.finder.presentation.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.test.github.user.finder.datamodule.model.dto.user.UserDto
import com.test.github.user.finder.datamodule.repository.user.UserRepository
import com.test.github.user.finder.framework.core.base.BaseDataSourceFactory

class UserDataFactory(
    var keySearch: String,
    private val userRepository: UserRepository,
    private val setRetryAfter: (
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: PageKeyedDataSource.LoadCallback<Int, UserDto.Items>,
        exception: Exception?,
        message: String?
    ) -> Unit,
    private val itemSize: Int
) : BaseDataSourceFactory<UserDto.Items>() {

    val userLiveData = MutableLiveData<UserDataSource>()
    override fun createDataSource(): DataSource<Int, UserDto.Items> {
        val dataSource = UserDataSource(
            keySearch,
            userRepository,
            setRetryAfter,
            itemSize
        )
        userLiveData.postValue(dataSource)

        return dataSource
    }
}