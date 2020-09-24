package com.test.github.user.finder.datamodule.model.dto.core

import androidx.annotation.Keep
import com.squareup.moshi.Json
import java.io.Serializable

@Keep
open class BaseApiDto : Serializable {
    @field:Json(name = "documentation_url")
    val documentation_url: String? = null

    @field:Json(name = "message")
    val message: String? = null
}