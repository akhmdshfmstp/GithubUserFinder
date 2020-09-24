package com.test.github.user.finder.framework.design

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.MalformedJsonException
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.test.github.user.finder.framework.R
import okhttp3.internal.http2.StreamResetException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoadingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    RelativeLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        injectViews()
    }

    enum class State {
        LOADING, ERROR, EMPTY
    }

    private var state: State = State.ERROR
    private var progressBar: LottieAnimationView? = null
    private var baseView: LinearLayout? = null
    private var backgroundImageView: ImageView? = null
    private var progressSubtitle: TextView? = null
    private var progressTitle: TextView? = null
    private var progressMessage: TextView? = null
    private var retryButton: Button? = null
    private var listener: OnRetryListener? = null
    private var imgInfo: ImageView? = null

    private fun injectViews() {
        inflate(context, R.layout.loading_view, this)
        progressBar = findViewById(R.id.lottie_progress)
        baseView = findViewById(R.id.view_loading)
        backgroundImageView = findViewById(R.id.img_background)
        progressSubtitle = findViewById(R.id.txt_progress_subtitle)
        progressTitle = findViewById(R.id.txt_title)
        progressMessage = findViewById(R.id.txt_message)
        retryButton = findViewById(R.id.btn_retry)
        retryButton?.setOnClickListener { onClickRetry() }
        imgInfo = findViewById(R.id.img_info)
        setDefaultRetrybutton()
        showLoading()
    }

    fun showLoading() {
        state = State.LOADING
        progressSubtitle?.text = context.getString(R.string.pl_loading)
        progressTitle?.text = null
        progressMessage?.text = null
        baseView?.background = ContextCompat.getDrawable(context, R.color.white)
        imgInfo?.visibility = View.GONE
        showButton()
        showProgress()
    }

    private fun showProgress() {
        progressBar?.visibility = when (state) {
            State.LOADING -> View.VISIBLE
            else          -> View.GONE
        }
        progressSubtitle?.visibility = when (state) {
            State.LOADING -> View.VISIBLE
            else          -> View.GONE
        }
        progressTitle?.visibility = when (state) {
            State.LOADING -> View.GONE
            else          -> View.VISIBLE
        }
        progressMessage?.visibility = when (state) {
            State.LOADING -> View.GONE
            else          -> View.VISIBLE
        }
        if (state == State.LOADING)
            backgroundImageView?.visibility = View.GONE
    }

    private fun showButton() {
        retryButton?.visibility = when (state) {
            State.LOADING -> View.GONE
            else          -> View.VISIBLE
        }
    }

    fun showEmpty(
        title: String,
        message: String,
        showButton: Boolean,
        buttonText: String? = null
    ) {
        state = State.EMPTY
        baseView?.background = ContextCompat.getDrawable(context, R.color.white)
        progressTitle?.text = title
        progressMessage?.text = message
        if (buttonText.isNullOrEmpty()) setDefaultRetrybutton()
        else retryButton?.text = buttonText
        retryButton?.visibility = when (showButton) {
            true -> View.VISIBLE
            false -> View.GONE
        }
        imgInfo?.visibility = View.VISIBLE
        imgInfo?.setImageResource(R.drawable.ic_not_found)
        showProgress()
    }

    @SuppressLint("LogNotTimber")
    fun showError(title: String? = null, message: String) {
        state = State.ERROR
        baseView?.background = ContextCompat.getDrawable(context, R.color.white)
        if (title == null) {
            progressTitle?.visibility = View.INVISIBLE
        } else {
            progressTitle?.text = title
            progressTitle?.visibility = View.VISIBLE
        }
        val splitSizeMessage = message.split("||").size
        val mess = if (splitSizeMessage == 2) {
            message.replace("||", "\n\n")
        } else {
            message
        }
        imgInfo?.visibility = View.VISIBLE
        imgInfo?.setImageResource(R.drawable.ic_server_error)

        progressMessage?.text = mess
        showButton()
        showProgress()
    }

    fun showError(exception: Exception?) {
        setDefaultRetrybutton()
        retryButton?.setOnClickListener { onClickRetry() }
        state = State.ERROR
        when (exception) {
            is UnknownHostException, is ConnectException -> {
                progressTitle?.text = context.getString(R.string.me_connection_title)
                progressMessage?.text = context.getString(R.string.me_connection)
                imgInfo?.visibility = View.VISIBLE
                imgInfo?.setImageResource(R.drawable.ic_connection_error)
            }
            is SocketTimeoutException -> {
                progressTitle?.text = context.getString(R.string.me_time_out_title)
                progressMessage?.text = context.getString(R.string.me_time_out)
                imgInfo?.visibility = View.VISIBLE
                imgInfo?.setImageResource(R.drawable.ic_connection_error)
            }
            is MalformedJsonException -> {
                progressTitle?.text = context.getString(R.string.me_unknown_title)
                progressMessage?.text = context.getString(R.string.me_unknown)
                imgInfo?.visibility = View.VISIBLE
                imgInfo?.setImageResource(R.drawable.ic_server_error)
            }
            is StreamResetException -> {
                progressTitle?.text = context.getString(R.string.me_server_down_title)
                progressMessage?.text = context.getString(R.string.me_server_down)
                imgInfo?.visibility = View.VISIBLE
                imgInfo?.setImageResource(R.drawable.ic_connection_error)
            }
            else                                         -> {
                progressTitle?.text = context.getString(R.string.me_server_title)
                progressMessage?.text = context.getString(R.string.me_server)
                imgInfo?.visibility = View.VISIBLE
                imgInfo?.setImageResource(R.drawable.ic_server_error)
            }
        }
        showButton()
        showProgress()
    }

    private fun setDefaultRetrybutton() {
        retryButton?.text = context.getString(R.string.action_retry)
    }

    fun setOnRetryListener(listener: OnRetryListener) {
        this.listener = listener
    }

    private fun onClickRetry() {
        listener?.let {
            showLoading()
            if (state == State.EMPTY) it.onClickEmpty() else it.onRetry()
        }
    }

    interface OnRetryListener {
        fun onRetry()

        fun onClickEmpty() {
            //Do something for empty state
        }
    }
}