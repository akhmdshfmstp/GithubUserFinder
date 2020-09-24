package com.test.github.user.finder.framework.extention


import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


fun EditText.afterTextChanged(afterTextChanged: (Editable) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            afterTextChanged.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //ignore
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //ignore
        }
    })
}