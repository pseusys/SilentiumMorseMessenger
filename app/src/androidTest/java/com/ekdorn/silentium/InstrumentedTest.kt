package com.ekdorn.silentium

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.ekdorn.silentium.activities.SilentActivity
import com.ekdorn.silentium.fragments.NotesAdapter
import com.ekdorn.silentium.managers.PreferenceManager
import com.ekdorn.silentium.managers.PreferenceManager.DAH_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.EOM_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.GAP_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.get
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.models.Contact
import org.hamcrest.Matchers.not
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class UsageScenariosTest {
    companion object {
        @BeforeClass
        fun setupUser() {
            UserManager.me = Contact("Test User", "+10001112233", 1L)
        }
    }

    @get:Rule
    val rule: ActivityScenarioRule<SilentActivity> = ActivityScenarioRule(SilentActivity::class.java)

    @Test
    fun signInTest() {
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.user_name)).check(matches(withText(UserManager.me.name)))
        onView(withId(R.id.user_contact)).check(matches(withText(UserManager.me.contact)))
    }

    @Test
    fun createNoteTest() {
        val prefs = PreferenceManager[InstrumentationRegistry.getInstrumentation().targetContext]
        val dit = prefs.get<Long>(DAH_LENGTH_KEY) / 2
        val gap = prefs.get<Long>(GAP_LENGTH_KEY) * 1.5
        val dah = prefs.get<Long>(DAH_LENGTH_KEY) * 1.5
        val eom = prefs.get<Long>(EOM_LENGTH_KEY) * 1.5

        for (i in 1 .. 3) onView(withId(R.id.input_button)).perform(touch(dit))
        onView(withId(R.id.input_button)).perform(wait(gap.toLong()))
        for (i in 1 .. 3) onView(withId(R.id.input_button)).perform(touch(dah.toLong()))
        onView(withId(R.id.input_button)).perform(wait(gap.toLong()))
        for (i in 1 .. 3) onView(withId(R.id.input_button)).perform(touch(dit))
        onView(withId(R.id.input_button)).perform(wait(eom.toLong()))
        onView(withId(R.id.input_card)).check(matches(not(isVisible())))

        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.nav_notes)).perform(click())
        onView(withId(R.id.recycler)).perform(scrollTo<NotesAdapter.ViewHolder>(hasDescendant(withText("sos"))))
    }
}