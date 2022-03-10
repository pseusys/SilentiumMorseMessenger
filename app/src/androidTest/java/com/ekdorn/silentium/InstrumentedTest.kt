package com.ekdorn.silentium

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ekdorn.silentium.activities.ProxyActivity
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.fragments.NotesAdapter
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class UsageScenariosTest {
    @get:Rule //TODO: add (order = 0)
    val callbackRule: CallbacksRule = CallbacksRule()

    @get:Rule //TODO: add (order = 1)
    val activityRule: ActivityScenarioRule<ProxyActivity> = ActivityScenarioRule(ProxyActivity::class.java)

    @Test
    fun userTest() {
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.user_name)).check(matches(withText("")))
        onView(withId(R.id.user_contact)).check(matches(withText(BuildConfig.AUTH_NUMBER)))
    }

    @Test
    fun notesTest() {
        val message = "sos"

        tapView(R.id.input_button, message.toMyteReadable())
        onView(withId(R.id.input_card)).check(matches(not(isVisible())))

        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.nav_notes)).perform(click())
        onView(withId(R.id.recycler)).perform(scrollTo<NotesAdapter.ViewHolder>(hasDescendant(withText(message))))

        val note = onView(withChild(withText(message)))
        note.perform(longClick())
        onView(withId(R.id.action_copy)).perform(click())
        assertClipboard(message)

        // TODO: add + check send dialog

        note.perform(swipeRight())
        note.check(doesNotExist())
    }

    @Test
    fun contactsTest() {
        // TODO: add + check contact
        // TODO: check external contacts search
    }

    @Test
    fun dialogsTest() {
        // TODO: check dialog
    }

    @Test
    fun messagesTest() {
        // TODO: check dialog
    }
}