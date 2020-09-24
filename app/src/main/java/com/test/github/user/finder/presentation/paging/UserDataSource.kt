package com.test.github.user.finder.presentation.paging

import com.google.gson.Gson
import com.test.github.user.finder.datamodule.model.dto.core.BaseApiDto
import com.test.github.user.finder.datamodule.model.dto.user.UserDto
import com.test.github.user.finder.datamodule.repository.user.UserRepository
import com.test.github.user.finder.framework.core.base.BasePageKeyedDataSource
import com.test.github.user.finder.framework.core.common.NetworkState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDataSource(
    private val keySearch: String,
    private val userRepository: UserRepository,
    private val setRetryAfter: (
        params: LoadParams<Int>,
        callback: LoadCallback<Int, UserDto.Items>,
        exception: Exception?,
        message: String?
    ) -> Unit,
    private val itemSize: Int,
    private val runLayoutAnimation: () -> Unit
) : BasePageKeyedDataSource<UserDto.Items>() {

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, UserDto.Items>
    ) {
        GlobalScope.launch {
            try {
                prepareLoadAfter()

                val pageNo = params.key
                val item = userRepository.getSearchListUserAsync(
                    keySearch, itemSize, pageNo
                )
                val response = item.await()
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        body.items.takeIf { data -> !data.isNullOrEmpty() }
                            ?.apply {
                                loadAfterSuccess()
                                val adjacentPageKey = if (body.total_count!!.div(100) == 0) {
                                    null
                                } else {
                                    if (body.total_count!!.div(100) == pageNo) {
                                        if (body.total_count!! % 100 == 0) {
                                            null
                                        } else {
                                            pageNo + 1
                                        }
                                    } else if (pageNo - 1 == body.total_count!!.div(100)) {
                                        null
                                    } else if (pageNo == 10) {
                                        null
                                    } else {
                                        pageNo + 1
                                    }
                                }
                                callback.onResult(this, adjacentPageKey)
                            } ?: run {
                            loadInitialEmpty()
                        }
                    }
                } else {
                    val body = response.errorBody()
                    val baseApiDto: BaseApiDto = Gson().fromJson(
                        body?.charStream(),
                        BaseApiDto::class.java
                    )
                    postAfterState(NetworkState.LOADED)
                    if (baseApiDto.message != null &&
                        !baseApiDto.message?.trim()?.isEmpty()!! &&
                        baseApiDto.documentation_url != null &&
                        !baseApiDto.documentation_url?.trim()?.isEmpty()!!
                    ) {
                        setRetryAfter(
                            params,
                            callback,
                            null,
                            baseApiDto.message + "||" + baseApiDto.documentation_url
                        )
                    } else {
                        setRetryAfter(params, callback, null, null)
                    }
                }
            } catch (e: Exception) {
                postAfterState(NetworkState.LOADED)
                setRetryAfter(params, callback, e, null)
            }
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, UserDto.Items>
    ) {
        if (keySearch.isEmpty()) return
        GlobalScope.launch {
            try {
                prepareLoadInitial()
                val pageNo = 1
                val item = userRepository.getSearchListUserAsync(
                    keySearch, itemSize, pageNo
                )
                val response = item.await()
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        body.items.takeIf { data -> !data.isNullOrEmpty() }
                            ?.apply {
                                loadInitialSuccess()
                                val nextPageKey = if (body.total_count!!.div(100) == 0) {
                                    null
                                } else {
                                    if (body.total_count!!.div(100) == pageNo) {
                                        if (body.total_count!! % 100 == 0) {
                                            null
                                        } else {
                                            pageNo + 1
                                        }
                                    } else if (pageNo - 1 == body.total_count!!.div(100)) {
                                        null
                                    } else if (pageNo == 10) {
                                        null
                                    } else {
                                        pageNo + 1
                                    }
                                }
                                runLayoutAnimation()
                                callback.onResult(this, null, nextPageKey)
                            } ?: run {
                            loadInitialEmpty()
                        }
                    }
                } else {
                    val body = response.errorBody()
                    val baseApiDto: BaseApiDto = Gson().fromJson(
                        body?.charStream(),
                        BaseApiDto::class.java
                    )
                    if (baseApiDto.message != null &&
                        !baseApiDto.message?.trim()?.isEmpty()!! &&
                        baseApiDto.documentation_url != null &&
                        !baseApiDto.documentation_url?.trim()?.isEmpty()!!
                    ) {
                        loadInitialError(
                            params,
                            callback,
                            message = baseApiDto.message + "||" + baseApiDto.documentation_url
                        )
                    } else {
                        loadInitialError(params, callback)
                    }
                }
            } catch (e: Exception) {
                loadInitialError(params, callback, e)
            }
        }
    }
}