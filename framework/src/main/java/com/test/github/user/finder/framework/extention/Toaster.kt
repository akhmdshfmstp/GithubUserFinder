package com.test.github.user.finder.framework.extention


import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Context.showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()