package com.ekdorn.silentium

import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ekdorn.silentium.activities.SilentActivity
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.fragments.NotesAdapter
import com.ekdorn.silentium.managers.UserManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
@LargeTest
class UsageScenariosTest {
    private val idlingResource = CountingIdlingResource("AUTH_RESOURCE")

    @get:Rule
    val rule: ActivityScenarioRule<SilentActivity> = ActivityScenarioRule(SilentActivity::class.java)

    @Before
    fun login() {
        if (Firebase.auth.currentUser == null) {
            val number = BuildConfig.AUTH_NUMBER
            val code = BuildConfig.AUTH_CODE
            Firebase.auth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(number, code)
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) = idlingResource.decrement()
                    override fun onVerificationFailed(p0: FirebaseException) = throw p0
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            idlingResource.increment()
            onIdle()
        }
    }

    @Test
    fun userTest() {
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.user_name)).check(matches(withText(UserManager.me.name)))
        onView(withId(R.id.user_contact)).check(matches(withText(UserManager.me.contact)))
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