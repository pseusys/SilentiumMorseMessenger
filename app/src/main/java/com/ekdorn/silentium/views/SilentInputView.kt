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
import com.ekdorn.silentium.core.*
import com.ekdorn.silentium.managers.PreferenceManager
import com.ekdorn.silentium.managers.PreferenceManager.DAH_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.END_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.EOM_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.GAP_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.get
import com.ekdorn.silentium.utils.pow
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

    private val prefs = PreferenceManager[context]
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
        delay(prefs.get<Long>(GAP_LENGTH_KEY))
        morseListener?.onLong(currentLong.biBitsToLong())
    }

    private fun setEndTimer() = MainScope().launch {
        delay(prefs.get<Long>(END_LENGTH_KEY))
        morseListener?.onLong(BiBit.END.atom.toLong())
    }

    private fun setEomTimer() = MainScope().launch {
        delay(prefs.get<Long>(EOM_LENGTH_KEY))
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
            if (delta >= prefs.get<Long>(END_LENGTH_KEY)) {
                input.addAll(currentLong)
                input.add(BiBit.END)
                currentLong.clear()
            } else if (delta >= prefs.get<Long>(GAP_LENGTH_KEY)) {
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
        if (delta >= prefs.get<Long>(DAH_LENGTH_KEY)) currentLong.add(BiBit.DAH)
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
