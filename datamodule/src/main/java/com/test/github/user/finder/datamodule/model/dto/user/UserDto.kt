package com.test.github.user.finder.datamodule.model.dto.user

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.test.github.user.finder.datamodule.model.dto.core.BaseApiDto

@Keep
class UserDto : BaseApiDto() {

    @field:Json(name = "total_count")
    var total_count: Int? = null

    @field:Json(name = "incomplete_results")
    var incomplete_results: Boolean? = null

    @field:Json(name = "items")
    var items: List<Items>? = null

    @Keep
    class Items {
        @field:Json(name = "login")
        var login: String? = null

        @field:Json(name = "id")
        var id: Int? = null

        @field:Json(name = "node_id")
        var node_id: String? = null

        @field:Json(name = "avatar_url")
        var avatar_url: String? = null

        @field:Json(name = "gravatar_id")
        var gravatar_id: String? = null

        @field:Json(name = "url")
        var url: String? = null

        @field:Json(name = "html_url")
        var html_url: String? = null

        @field:Json(name = "followers_url")
        var followers_url: String? = null

        @field:Json(name = "following_url")
        var following_url: String? = null

        @field:Json(name = "gists_url")
        var gists_url: String? = null

        @field:Json(name = "starred_url")
        var starred_url: String? = null

        @field:Json(name = "subscriptions_url")
        var subscriptions_url: String? = null

        @field:Json(name = "organizations_url")
        var organizations_url: String? = null

        @field:Json(name = "repos_url")
        var repos_url: String? = null

        @field:Json(name = "events_url")
        var events_url: String? = null

        @field:Json(name = "received_events_url")
        var received_events_url: String? = null

        @field:Json(name = "type")
        var type: String? = null

        @field:Json(name = "site_admin")
        var site_admin: Boolean? = null

        @field:Json(name = "score")
        var score: Double? = null
    }


}