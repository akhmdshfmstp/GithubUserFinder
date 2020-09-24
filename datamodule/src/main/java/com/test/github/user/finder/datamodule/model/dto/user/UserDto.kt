package com.test.github.user.finder.datamodule.model.dto.user

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.test.github.user.finder.datamodule.model.dto.core.BaseApiDto

@Keep
class UserDto : BaseApiDto() {

    @field:Json(name = "total_count")
    var total_count: Int? = null

    @field:Json(name = "items")
    var items: List<Items>? = null

    @Keep
    class Items {
        @field:Json(name = "login")
        var login: String? = null

        @field:Json(name = "id")
        var id: Int? = null

        @field:Json(name = "avatar_url")
        var avatar_url: String? = null
    }


}