package com.test.github.user.finder.presentation.view

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.test.github.user.finder.framework.core.view.LifecycleView
import com.test.github.user.finder.framework.design.LoadingView
import com.test.github.user.finder.presentation.adapter.UserAdapter

interface MainView : LifecycleView {
    var retryListener: LoadingView.OnRetryListener
    var onRefreshListener: SwipeRefreshLayout.OnRefreshListener
    var onTouchListener: View.OnTouchListener
    var layoutManager: LinearLayoutManager
    var listAdapter: UserAdapter
    fun onSearchClicked(view: View)
}