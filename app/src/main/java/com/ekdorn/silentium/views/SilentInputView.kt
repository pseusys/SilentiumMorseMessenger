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
import com.ekdorn.silentium.activities.SilentActivity
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.*
import com.ekdorn.silentium.pow
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import kotlin.math.sqrt


class SilentInputView(context: Context, attributes: AttributeSet?, style: Int) : AppCompatImageView(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    private var morseListener: MorseListener? = null
    private var rotate = false

    private val DAH_LENGTH: Int
    private val GAP_LENGTH: Int
    private val END_LENGTH: Int
    private val EOM_LENGTH: Int

    private val input = mutableListOf<BiBit>()
    private val currentLong = mutableListOf<BiBit>()

    private var inputInProgress = false
    private var previousTouch = 0L
    private var gapTimer: Job? = null
    private var endTimer: Job? = null
    private var eomTimer: Job? = null

    init {
        context.theme.obtainStyledAttributes(attributes, R.styleable.SilentInputView, 0, 0).apply {
            try { rotate = getBoolean(R.styleable.SilentInputView_rotate, rotate) } finally { recycle() }
        }

        if (rotate) ObjectAnimator.ofFloat(this, View.ROTATION, 0.0f, 360.0f).apply {
            duration = 60000
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }.start()

        val sharedPref = context.getSharedPreferences(SilentActivity.PREFERENCES_FILE, Context.MODE_PRIVATE)
        DAH_LENGTH = sharedPref.getInt(Morse.DAH_LENGTH_KEY.first, Morse.DAH_LENGTH_KEY.second)
        GAP_LENGTH = sharedPref.getInt(Morse.GAP_LENGTH_KEY.first, Morse.GAP_LENGTH_KEY.second)
        END_LENGTH = sharedPref.getInt(Morse.END_LENGTH_KEY.first, Morse.END_LENGTH_KEY.second)
        EOM_LENGTH = sharedPref.getInt(Morse.EOM_LENGTH_KEY.first, Morse.EOM_LENGTH_KEY.second)
    }

    private var isUp = false
    private fun animateUp () {
        if (!isUp) animate().scaleX(0.8F).scaleY(0.8F).setInterpolator(DecelerateInterpolator(2F)).setDuration(750).start()
        isUp = true
    }
    private fun animateDown () {
        if (isUp) animate().scaleX(1F).scaleY(1F).setDuration(750).start()
        isUp = false
    }

    private fun touchedRound (tX: Float, tY: Float) = sqrt(((width / 2F - tX) pow 2F) + ((height / 2F - tY) pow 2F)) < width / 2F

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent (event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> if (touchedRound(event.x, event.y)) {
                animateUp()
                actionDown()
            }
            MotionEvent.ACTION_UP -> {
                animateDown()
                actionUp()
            }
            MotionEvent.ACTION_MOVE -> if (!touchedRound(event.x, event.y)) {
                animateDown()
                actionUp()
            }
            else -> return super.onTouchEvent(event)
        }
        return true
    }


    private fun setGapTimer() = MainScope().launch {
        delay(GAP_LENGTH.toLong())
        morseListener?.onLong(currentLong.biBitsToLong())
    }

    private fun setEndTimer() = MainScope().launch {
        delay(END_LENGTH.toLong())
        morseListener?.onLong(BiBit.END.atom.toLong())
    }

    private fun setEomTimer() = MainScope().launch {
        delay(EOM_LENGTH.toLong())
        input.addAll(currentLong)
        currentLong.clear()
        morseListener?.onMyte(input.biBitsToMyte())
        input.clear()
        inputInProgress = false
    }


    private fun actionDown() {
        if (!inputInProgress) {
            inputInProgress = true
            morseListener?.onStart()
        } else {
            val delta = currentTimeMillis() - previousTouch
            if (delta >= END_LENGTH) {
                input.addAll(currentLong)
                input.add(BiBit.END)
                currentLong.clear()
            } else if (delta >= GAP_LENGTH) {
                input.addAll(currentLong)
                input.add(BiBit.GAP)
                currentLong.clear()
            }
        }
        gapTimer?.cancel()
        endTimer?.cancel()
        eomTimer?.cancel()
        previousTouch = currentTimeMillis()
    }

    private fun actionUp() {
        val delta = currentTimeMillis() - previousTouch
        if (delta >= DAH_LENGTH) currentLong.add(BiBit.DAH)
        else currentLong.add(BiBit.DIT)
        morseListener?.onBiBit(currentLong.lastOrNull())

        gapTimer?.cancel()
        gapTimer = setGapTimer()
        endTimer?.cancel()
        endTimer = setEndTimer()
        eomTimer?.cancel()
        eomTimer = setEomTimer()

        previousTouch = currentTimeMillis()
    }

    fun addMorseListener(listener: MorseListener) { morseListener = listener }


    open class MorseListener {
        open fun onStart () {}
        open fun onBiBit (biBit: BiBit?) {}
        open fun onLong (long: Long?) {}
        open fun onMyte (myte: Myte) {}
    }
}
