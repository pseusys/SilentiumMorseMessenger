package com.ekdorn.silentium.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import com.ekdorn.silentium.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * The button cyclically runs a clickListener, emulating keyboard-like behaviour. First
 * click is fired immediately, next one after the initialInterval, and subsequent
 * ones after the normalInterval.
 * Interval is scheduled after the onClick completes, so it has to run fast.
 */
class RepeatButton(context: Context, attributes: AttributeSet?, style: Int) : AppCompatImageButton(context, attributes, style), View.OnTouchListener {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, R.attr.imageButtonStyle)
    constructor(context: Context): this(context, null)

    private val initialInterval: Long
    private val normalInterval: Long

    private var clickListener: OnClickListener? = null
    private var timer: Job? = null

    init {
        context.theme.obtainStyledAttributes(attributes, R.styleable.RepeatButton, 0, 0).apply {
            try {
                initialInterval = getInt(R.styleable.RepeatButton_initialInterval, 400).toLong()
                normalInterval = getInt(R.styleable.RepeatButton_normalInterval, 50).toLong()
            } finally { recycle() }
        }
        require(!(initialInterval < 0 || normalInterval < 0)) { "Negative interval!" }
        setOnTouchListener(this)
    }

    private fun setTimer(interval: Long): Job = MainScope().launch {
        delay(interval)
        clickListener?.onClick(this@RepeatButton)
        timer = setTimer(normalInterval)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent) = when (motionEvent.action) {
        MotionEvent.ACTION_DOWN -> {
            timer?.cancel()
            clickListener?.onClick(this)
            timer = setTimer(initialInterval)
            true
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            timer?.cancel()
            true
        }
        else -> false
    }

    override fun setOnClickListener(l: OnClickListener?) { clickListener = l }
}
