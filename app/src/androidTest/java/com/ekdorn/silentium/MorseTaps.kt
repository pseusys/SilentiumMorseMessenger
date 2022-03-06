package com.ekdorn.silentium

import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher


fun touch(length: Long): ViewAction = ViewActions.actionWithAssertions(GeneralClickAction(
    MorseTapper(length),
    GeneralLocation.VISIBLE_CENTER,
    Press.FINGER,
    InputDevice.SOURCE_UNKNOWN,
    MotionEvent.BUTTON_PRIMARY
))

fun wait(delay: Long) = object : ViewAction {
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
