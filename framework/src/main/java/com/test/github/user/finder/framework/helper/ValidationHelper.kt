package com.test.github.user.finder.framework.helper

object ValidationHelper {

    fun isEmpty(text: String?): Boolean {
        if (text.isNullOrEmpty()) return true
        if (text.trim().isEmpty()) return true
        return false
    }

}