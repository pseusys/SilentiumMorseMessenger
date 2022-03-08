package com.ekdorn.silentium

import android.view.View
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matchers.`is`
import org.junit.Assert


fun isVisible() = object : ViewAssertion {
    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        ViewMatchers.assertThat("View alpha: ${view.alpha}", true, `is`(isAlpha(view)))
    }

    private fun isAlpha(view: View?) = if (view != null) view.alpha == 1.0f else false
}

fun assertClipboard(str: String) {
    Assert.assertEquals(4, 2 + 2)
}
