package com.test.github.user.finder.datamodule.repository.user

import com.test.github.user.finder.datamodule.service.user.UserService
import com.test.github.user.finder.datamodule.model.dto.user.UserDto
import kotlinx.coroutines.Deferred
import retrofit2.Response

interface UserRepository {

    fun getSearchListUserAsync(
        keyword: String,
        perPage: Int,
        page: Int
    ): Deferred<Response<UserDto>>

}

class UserRepositoryImpl(private val userService: UserService) : UserRepository {

    override fun getSearchListUserAsync(
        keyword: String,
        perPage: Int,
        page: Int
    ) = userService.getSearchListUserAsync(
        keyword,
        perPage,
        page
    )

}