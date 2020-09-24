package com.test.github.user.finder.framework.helper

import android.content.Context
import com.test.github.user.finder.framework.R

object Common {

    fun isScreenNotTab(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.portrait_only)
    }

}