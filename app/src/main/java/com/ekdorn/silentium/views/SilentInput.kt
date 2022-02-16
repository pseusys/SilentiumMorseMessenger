package com.ekdorn.silentium.views

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.morse.MorseListener
import kotlin.math.pow
import kotlin.math.sqrt


private infix fun Float.pow(x: Float) = pow(x)

class SilentInput(context: Context, attributes: AttributeSet?, style: Int) : AppCompatImageView(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    private val listener = MorseListener(context)
    private var rotate = false

    init {
        context.theme.obtainStyledAttributes(attributes, R.styleable.SilentInput, 0, 0).apply {
            try { rotate = getBoolean(R.styleable.SilentInput_rotate, rotate) } finally { recycle() }
        }

        if (rotate) ObjectAnimator.ofFloat(this, View.ROTATION, 0.0f, 360.0f).apply {
            duration = 60000
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }.start()
    }

    private var isUp = false
    private fun animateUp() {
        if (!isUp) animate().scaleX(0.8F).scaleY(0.8F).setInterpolator(DecelerateInterpolator(2F)).setDuration(750).start()
        isUp = true
    }
    private fun animateDown() {
        if (isUp) animate().scaleX(1F).scaleY(1F).setDuration(750).start()
        isUp = false
    }

    private fun touchedRound (tX: Float, tY: Float) = sqrt(((width / 2F - tX) pow 2F) + ((height / 2F - tY) pow 2F)) < width / 2F

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> if (touchedRound(event.x, event.y)) {
                animateUp()
                return listener.decode(event)
            }
            MotionEvent.ACTION_UP -> {
                animateDown()
                return listener.decode(event)
            }
            MotionEvent.ACTION_MOVE -> if (!touchedRound(event.x, event.y)) {
                animateDown()
                return listener.decode(event)
            }
        }
        return super.onTouchEvent(event)
    }
}
