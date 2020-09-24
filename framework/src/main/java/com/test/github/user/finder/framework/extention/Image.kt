package com.test.github.user.finder.framework.extention

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.test.github.user.finder.framework.R


fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .override(75)
        .placeholder(R.drawable.ic_user)
        .circleCrop()
        .into(this)
}
