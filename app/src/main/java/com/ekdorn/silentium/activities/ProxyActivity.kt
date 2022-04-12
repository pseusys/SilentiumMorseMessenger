package com.ekdorn.silentium.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.ekdorn.silentium.BuildConfig.APPLICATION_ID
import com.ekdorn.silentium.R
import com.ekdorn.silentium.fragments.SettingsFragment
import com.ekdorn.silentium.managers.CryptoManager
import com.ekdorn.silentium.managers.NetworkManager
import com.ekdorn.silentium.utils.observe
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.auth.util.ExtraConstants
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class ProxyActivity : AppCompatActivity() {
    private lateinit var action: String
    private val observer = observe(FirebaseAuthUIActivityResultContract::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { true }
        super.onCreate(savedInstanceState)
        action = intent.action.toString()

        if ((action != Intent.ACTION_APPLICATION_PREFERENCES) && ((intent.flags == 0) || intent.hasExtra(SettingsFragment.keyboard))) navigate(SettingsFragment.keyboard)
        else if (Firebase.auth.currentUser == null) {
            if (!checkDeepLink()) authenticate()
        } else lifecycleScope.launch {
            NetworkManager.updateUser(true)
            navigate(if (action == Intent.ACTION_APPLICATION_PREFERENCES) SettingsFragment.default else null)
        }
    }

    private fun authenticate() {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName(APPLICATION_ID, true, null)
            .setHandleCodeInApp(true)
            .setUrl("https://silentiumdynamic.page.link")
            .build()
        val providers = arrayListOf(
            EmailBuilder().setRequireName(false).enableEmailLinkSignIn().setActionCodeSettings(actionCodeSettings).build(),
            PhoneBuilder().build(),
            GoogleBuilder().build()
        )

        AuthUI.getInstance().silentSignIn(this, providers).addOnCompleteListener {
            if (it.isSuccessful) publishUser()
            else {
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.drawable.logo_crop)
                    .setTheme(R.style.Theme_SilentiumMorseMessenger)
                observer.launch(FirebaseAuthUIActivityResultContract::class, signInIntent.build(), ::onSignInResult)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun checkDeepLink() = if (AuthUI.canHandleIntent(intent) && (intent.extras != null)) {
        val link = intent.extras!!.getString(ExtraConstants.EMAIL_LINK_SIGN_IN)
        if (link != null) {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setEmailLink(link)
                .setAvailableProviders(emptyList())
            observer.launch(FirebaseAuthUIActivityResultContract::class, signInIntent.build(), ::onSignInResult)
        }
        true
    } else false

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) = when {
        result?.resultCode == RESULT_OK -> {
            publishUser()
            Unit
        }
        result?.idpResponse != null -> Toast.makeText(this, "Log in error!!", Toast.LENGTH_SHORT).show()
        else -> finish()
    }

    private fun publishUser() = lifecycleScope.launch {
        NetworkManager.updateUser(true, name = Firebase.auth.currentUser!!.displayName, photo = Firebase.auth.currentUser!!.photoUrl?.toString(), contact = this@ProxyActivity, key = CryptoManager.saveKey(this@ProxyActivity))
    }.invokeOnCompletion {
        if (it != null) {
            Toast.makeText(this, "Server connection error!!", Toast.LENGTH_SHORT).show()
            AuthUI.getInstance().signOut(this@ProxyActivity)
        } else navigate(if (action == Intent.ACTION_APPLICATION_PREFERENCES) SettingsFragment.default else null)
    }

    private fun navigate(extra: String?) {
        val intent = Intent(this, SilentActivity::class.java)
        if (extra != null) intent.putExtra(SilentActivity.NAVIGATE_TO_SETTINGS, extra)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}
