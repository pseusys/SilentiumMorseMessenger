package com.ekdorn.silentium.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ekdorn.silentium.BuildConfig.APPLICATION_ID
import com.ekdorn.silentium.R
import com.ekdorn.silentium.managers.CryptoManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.auth.util.ExtraConstants
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProxyActivity : AppCompatActivity() {
    private lateinit var action: String
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res -> onSignInResult(res) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { true }
        super.onCreate(savedInstanceState)
        action = intent.action.toString()
        if (Firebase.auth.currentUser == null) {
            if (!checkDeepLink()) authenticate()
        } else navigate()
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

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.logo_crop)
            .setTheme(R.style.Theme_SilentiumMorseMessenger)
        signInLauncher.launch(signInIntent.build())
    }

    @SuppressLint("RestrictedApi")
    private fun checkDeepLink() = if (AuthUI.canHandleIntent(intent) && (intent.extras != null)) {
        val link = intent.extras!!.getString(ExtraConstants.EMAIL_LINK_SIGN_IN)
        if (link != null) {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setEmailLink(link)
                .setAvailableProviders(emptyList())
            signInLauncher.launch(signInIntent.build())
        }
        true
    } else false

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) = when {
        result.resultCode == RESULT_OK -> {
            if (!CryptoManager.keysSaved()) CryptoManager.saveKeys(this)
            navigate()
        }
        result.idpResponse != null -> Toast.makeText(this, "Log in error!!", Toast.LENGTH_SHORT).show()
        else -> finish()
    }

    private fun navigate(intentFlags: Int = 0) {
        val intent = Intent(this, SilentActivity::class.java).addFlags(intentFlags)
        if (action == Intent.ACTION_APPLICATION_PREFERENCES) intent.putExtra(SilentActivity.NAVIGATE_TO_SETTINGS, true)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}
