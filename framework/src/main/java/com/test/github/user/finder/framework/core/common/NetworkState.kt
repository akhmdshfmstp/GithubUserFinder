package com.test.github.user.finder.framework.core.common

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val exception: Exception? = null,
    val message: String? = null
) {
    companion object {
        val LOADED =
            NetworkState(
                Status.SUCCESS
            )
        val EMPTY = NetworkState(
            Status.EMPTY
        )
        val LOADING =
            NetworkState(
                Status.RUNNING
            )
        val UNKNOWN =
            NetworkState(
                Status.UNKNOWN
            )
        fun error(exception: Exception?) =
            NetworkState(
                Status.FAILED,
                exception
            )

        fun error(message: String?) =
            NetworkState(
                Status.UNSUCCESSFUL,
                message = message
            )

        fun unknownError() =
            NetworkState(
                Status.UNKNOWN
            )
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        EMPTY,
        FAILED,
        UNSUCCESSFUL,
        UNKNOWN
    }
}