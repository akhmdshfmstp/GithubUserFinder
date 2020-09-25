package com.test.github.user.finder.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.MalformedJsonException
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
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

    private val REQ_CODE_SPEECH_INPUT = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        swipeListState()
        setToolbar()
        observeProgressStatus()
        observeData()
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

    @SuppressLint("ClickableViewAccessibility")
    override var onTouchListener = View.OnTouchListener { _, event ->
        val drawableRight = 2
        if (event.action == MotionEvent.ACTION_UP && event.rawX >= binding.textInputSearchUser.right -
            binding.textInputSearchUser.compoundDrawables[drawableRight].bounds.width()
        ) {
            if (!viewModel.clearKeySearch()) {
                searchByVoice()
            }
            return@OnTouchListener true
        }
        return@OnTouchListener false
    }

    override var onEditorActionListener = TextView.OnEditorActionListener { view, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchUser(view)
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    override var textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            binding.textInputSearchUser.apply {
                if (binding.textInputSearchUser.text.toString().startsWith(" ")) {
                    setText(
                        text.toString().replace(
                            " ", ""
                        )
                    )
                    setSelection(
                        text.toString().trim().length
                    )
                } else {
                    s?.length?.let { length ->
                        setSelection(length)
                    }
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // ignore
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // ignore
        }
    }

    override var listAdapter = UserAdapter(::onItemClicked) {
        viewModel.retry()
    }

    override fun onSearchClicked(view: View) {
        searchUser(view)
    }

    private fun swipeListState(value: Boolean? = false) {
        binding.swipeList.isEnabled = value!!
    }

    private fun swipeListDone() {
        binding.swipeList.isRefreshing = false
    }

    private fun searchUser(view: View? = null, key: String? = null) {
        key?.let { value ->
            viewModel.setSearch(value)
        }
        if (viewModel.isValid()) {
            ViewHelper.hideKeyboard(this, view ?: binding.textInputSearchUser)
            viewModel.searchUserByKeyword(
                ::setRetryAfter, ::runLayoutAnimation
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
                        swipeListState(true)
                        swipeListDone()
                        viewModel.hideLoading()
                    }
                    NetworkState.EMPTY -> {
                        viewModel.showLoading()
                        swipeListDone()
                        binding.loadingView.showEmpty(
                            getString(R.string.me_title_oops),
                            getString(R.string.not_found),
                            false
                        )
                    }
                    NetworkState.UNKNOWN -> {
                        viewModel.showLoading()
                        swipeListDone()
                        binding.loadingView.showError(
                            getString(R.string.me_title_oops),
                            getString(R.string.me_unknown)
                        )
                    }
                    else                 -> {
                        it.message?.let { message ->
                            swipeListDone()
                            viewModel.showLoading()
                            binding.loadingView.showError(
                                getString(R.string.me_title_oops),
                                message
                            )
                        }
                        it.exception?.let { e ->
                            swipeListDone()
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
                    binding.userList.smoothScrollToPosition(listAdapter.itemCount)
                }
            }
        }
    }

    private fun observeData() {
        observeData(
            viewModel.getSearchListUser(
                ::setRetryAfter, ::runLayoutAnimation
            )
        ) {
            if (it != null) {
                listAdapter.submitList(it)
            }
        }
    }

    private fun searchByVoice() {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1000)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.header_voice_suggestion)
        )
        try {
            startActivityForResult(
                speechRecognizerIntent,
                REQ_CODE_SPEECH_INPUT
            )
        } catch (a: ActivityNotFoundException) {
            showToast(getString(R.string.me_not_support_voice))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && null !=
            data
        ) {
            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let {
                searchUser(key = it[0])
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

    private fun runLayoutAnimation() {
        val animSlideUp = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_slide_from_bottom)
        binding.userList.apply {
            layoutAnimation = animSlideUp
            scheduleLayoutAnimation()
        }
    }

}