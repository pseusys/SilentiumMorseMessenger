package com.ekdorn.silentium

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.runner.lifecycle.ActivityLifecycleCallback
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.ekdorn.silentium.activities.SilentActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.lang.Exception
import java.util.concurrent.TimeUnit


class CallbacksRule: TestWatcher() {
    private val idlingResource = CountingIdlingResource("AUTH_RESOURCE")

    override fun starting(description: Description?) {
        IdlingRegistry.getInstance().register(idlingResource)
        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(callback)
        super.starting(description)
    }

    override fun finished(description: Description?) {
        IdlingRegistry.getInstance().unregister(idlingResource)
        ActivityLifecycleMonitorRegistry.getInstance().removeLifecycleCallback(callback)
        super.starting(description)
    }

    private val callback = ActivityLifecycleCallback { activity, stage ->
        if ((activity.javaClass == SilentActivity::class.java) && (stage == Stage.PRE_ON_CREATE) && (Firebase.auth.currentUser == null)) {
            val number = BuildConfig.AUTH_NUMBER
            val code = BuildConfig.AUTH_CODE
            Firebase.auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
            Firebase.auth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(number, code)
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onCodeAutoRetrievalTimeOut(p0: String) {
                        throw Exception(p0)
                    }

                    override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        throw Exception(p0)
                    }

                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        idlingResource.decrement()
                        throw Exception(Firebase.auth.currentUser.toString())
                    }
                    override fun onVerificationFailed(p0: FirebaseException) {
                        throw Exception("Exception happeneD!!")
                    }
                })
                .setActivity(activity)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            idlingResource.increment()
            Espresso.onIdle()
        }
    }
}
