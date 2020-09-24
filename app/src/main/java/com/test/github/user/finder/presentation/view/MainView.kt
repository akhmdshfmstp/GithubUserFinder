package com.test.github.user.finder.presentation.view

import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.test.github.user.finder.framework.core.view.LifecycleView
import com.test.github.user.finder.framework.design.LoadingView
import com.test.github.user.finder.presentation.adapter.UserAdapter

interface MainView : LifecycleView {
    var retryListener: LoadingView.OnRetryListener
    var onRefreshListener: SwipeRefreshLayout.OnRefreshListener
    var onTouchListener: View.OnTouchListener
    var onEditorActionListener: TextView.OnEditorActionListener
    var textWatcher: TextWatcher
    var layoutManager: LinearLayoutManager
    var listAdapter: UserAdapter
    fun onSearchClicked(view: View)
}