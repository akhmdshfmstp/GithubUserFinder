package com.test.github.user.finder.presentation

import android.os.Bundle
import android.util.MalformedJsonException
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.test.github.user.finder.R
import com.test.github.user.finder.databinding.ActivityMainBinding
import com.test.github.user.finder.datamodule.model.dto.user.UserDto
import com.test.github.user.finder.framework.core.base.BaseActivity
import com.test.github.user.finder.framework.core.common.NetworkState
import com.test.github.user.finder.framework.core.owner.ViewDataBindingOwner
import com.test.github.user.finder.framework.core.owner.ViewModelOwner
import com.test.github.user.finder.framework.design.LoadingView
import com.test.github.user.finder.framework.extention.afterTextChanged
import com.test.github.user.finder.framework.extention.showToast
import com.test.github.user.finder.framework.helper.ViewHelper
import com.test.github.user.finder.presentation.adapter.UserAdapter
import com.test.github.user.finder.presentation.view.MainView
import com.test.github.user.finder.presentation.viewmodel.MainViewModel
import okhttp3.internal.http2.StreamResetException
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class MainActivity : BaseActivity(),
    MainView,
    ViewModelOwner<MainViewModel>,
    ViewDataBindingOwner<ActivityMainBinding> {

    override fun getViewLayoutResId() = R.layout.activity_main

    override lateinit var binding: ActivityMainBinding
    override val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar()
        observeProgressStatus()
        observeData()
        setListener()
    }

    override var retryListener: LoadingView.OnRetryListener = object : LoadingView.OnRetryListener {
        override fun onRetry() {
            viewModel.refresh()
        }
    }

    override var onRefreshListener: SwipeRefreshLayout.OnRefreshListener =
        SwipeRefreshLayout.OnRefreshListener {
            viewModel.refresh()
        }

    override var layoutManager = LinearLayoutManager(
        this,
        RecyclerView.VERTICAL,
        false
    )

    override var listAdapter = UserAdapter(::onItemClicked) {
        viewModel.retry()
    }

    override fun onSearchClicked(view: View) {
        searchUser(view)
    }

    private fun searchUser(view: View) {
        if (viewModel.isValid()) {
            ViewHelper.hideKeyboard(this, view)
            viewModel.searchUserByKeyword(
                ::setRetryAfter
            )
        } else {
            showToast(getString(R.string.me_search_key_required))
        }
    }

    private fun onItemClicked(data: UserDto.Items) {
        data.login?.let { user ->
            showToast(user)
        }
    }

    private fun setToolbar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        binding.toolbar.apply {
            title = getString(R.string.app_name)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun observeProgressStatus() {
        observeData(viewModel.getInitialState()) { networkState ->
            networkState?.let {
                when (it) {
                    NetworkState.LOADING -> {
                        viewModel.showLoading()
                        binding.loadingView.showLoading()
                    }
                    NetworkState.LOADED -> {
                        viewModel.hideLoading()
                    }
                    NetworkState.EMPTY -> {
                        viewModel.showLoading()
                        binding.loadingView.showEmpty(
                            getString(R.string.me_title_oops),
                            getString(R.string.not_found),
                            false
                        )
                    }
                    NetworkState.UNKNOWN -> {
                        viewModel.showLoading()
                        binding.loadingView.showError(
                            getString(R.string.me_title_oops),
                            getString(R.string.me_unknown)
                        )
                    }
                    else                 -> {
                        it.message?.let { message ->
                            viewModel.showLoading()
                            binding.loadingView.showError(
                                getString(R.string.me_title_oops),
                                message
                            )
                        }
                        it.exception?.let { e ->
                            viewModel.showLoading()
                            binding.loadingView.showError(e)
                        }
                    }
                }
            }
        }

        observeData(viewModel.getNetworkState()) { networkState ->
            networkState?.let {
                listAdapter.setNetworkState(it)
                if (it.exception != null || it.message != null) {
                    binding.recyclerViewUpcomingHeader.smoothScrollToPosition(listAdapter.itemCount)
                }
            }
        }
    }

    private fun observeData() {
        observeData(
            viewModel.getSearchListUser(
                ::setRetryAfter
            )
        ) { upcomingHeaderList ->
            if (upcomingHeaderList != null) {
                listAdapter.submitList(upcomingHeaderList)
            }
        }
    }

    private fun setListener() {
        binding.textInputSearchUser.apply {
            setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    searchUser(this)
                }
                false
            }
            afterTextChanged {
                if (text.toString().startsWith(" ")) {
                    setText(
                        text.toString().replace(
                            " ", ""
                        )
                    )
                    setSelection(
                        text.toString().trim().length
                    )
                }
            }
        }
    }

    private fun setRetryAfter(
        params: PageKeyedDataSource.LoadParams<Int>,
        callback: PageKeyedDataSource.LoadCallback<Int, UserDto.Items>,
        exception: Exception? = null,
        message: String? = null
    ) {
        viewModel.setParams(params)
        viewModel.setCallback(callback)
        showSnackbar(exception, message)
    }

    private fun showSnackbar(exception: Exception? = null, message: String? = null) {
        val actionMessage = getString(R.string.action_retry)
        val titleMessage = message?.takeIf { it.isNotEmpty() }?.apply {
            this
        } ?: run {
            if (exception == null) {
                getString(R.string.me_unknown)
            } else {
                when (exception) {
                    is UnknownHostException, is ConnectException, is SocketTimeoutException -> {
                        getString(R.string.me_connection)
                    }
                    is MalformedJsonException -> {
                        getString(R.string.me_unknown)
                    }
                    is StreamResetException -> {
                        getString(R.string.me_server_down)
                    }
                    else                                                                    -> {
                        getString(R.string.me_server)
                    }
                }
            }
        }

        Snackbar.make(
            binding.constraintRootView,
            titleMessage,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(actionMessage) {
                if (actionMessage == getString(R.string.action_retry)) viewModel.retryAfter()
                else dismiss()
            }
            view.findViewById<TextView>(
                com.google.android.material.R.id.snackbar_text
            ).textSize = 13f
            view.findViewById<TextView>(
                com.google.android.material.R.id.snackbar_action
            ).apply {
                textSize = 13f
                setTextColor(
                    ContextCompat.getColorStateList(
                        this@MainActivity,
                        R.color.colorAccent
                    )
                )
            }

            show()
        }
    }
}