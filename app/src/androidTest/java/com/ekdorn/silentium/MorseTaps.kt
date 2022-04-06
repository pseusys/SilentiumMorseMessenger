package com.ekdorn.silentium

import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.platform.app.InstrumentationRegistry
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.core.toBiBits
import com.ekdorn.silentium.managers.PreferenceManager
import org.hamcrest.Matcher


fun tapView(id: Int, myte: Myte) {
    val prefs = PreferenceManager[InstrumentationRegistry.getInstrumentation().targetContext]
    val dit = prefs.get(R.string.pref_morse_dah_key, -1) / 2
    val gap = prefs.get(R.string.pref_morse_gap_key, -1) * 1.5
    val dah = prefs.get(R.string.pref_morse_dah_key, -1) * 1.5
    val end = prefs.get(R.string.pref_morse_end_key, -1) * 1.5
    val eom = prefs.get(R.string.pref_morse_eom_key, -1) * 1.5
    myte.toBiBits().forEach {
        when (it) {
            BiBit.DIT -> onView(ViewMatchers.withId(id)).perform(touch(dit.toLong()))
            BiBit.DAH -> onView(ViewMatchers.withId(id)).perform(touch(dah.toLong()))
            BiBit.GAP -> onView(ViewMatchers.withId(id)).perform(wait(gap.toLong()))
            BiBit.END -> onView(ViewMatchers.withId(id)).perform(wait(end.toLong()))
        }
    }
    onView(ViewMatchers.withId(id)).perform(wait(eom.toLong()))
}

private fun touch(length: Long): ViewAction = ViewActions.actionWithAssertions(GeneralClickAction(
    MorseTapper(length),
    GeneralLocation.VISIBLE_CENTER,
    Press.FINGER,
    InputDevice.SOURCE_UNKNOWN,
    MotionEvent.BUTTON_PRIMARY
))

private fun wait(delay: Long) = object : ViewAction {
    override fun getConstraints(): Matcher<View> = isDisplayed()
    override fun getDescription(): String = "wait for $delay milliseconds"
    override fun perform(uiController: UiController, v: View?) {
        uiController.loopMainThreadForAtLeast(delay)
    }
}

private class MorseTapper(private val length: Long): Tapper {
    override fun sendTap(uiController: UiController, coordinates: FloatArray?, precision: FloatArray?): Tapper.Status {
        return sendTap(uiController, coordinates, precision, 0, 0)
    }

    override fun sendTap(uiController: UiController, coordinates: FloatArray?, precision: FloatArray?, inputDevice: Int, buttonState: Int): Tapper.Status {
        val downEvent = MotionEvents.sendDown(uiController, coordinates, precision, inputDevice, buttonState).down
        try {
            uiController.loopMainThreadForAtLeast(length)
            if (!MotionEvents.sendUp(uiController, downEvent)) {
                MotionEvents.sendCancel(uiController, downEvent)
                return Tapper.Status.FAILURE
            }
        } finally {
            downEvent!!.recycle()
        }
        return Tapper.Status.SUCCESS
    }
}
