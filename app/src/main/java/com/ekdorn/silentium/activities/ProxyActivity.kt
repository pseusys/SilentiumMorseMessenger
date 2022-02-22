package com.ekdorn.silentium.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ekdorn.silentium.BuildConfig.APPLICATION_ID
import com.ekdorn.silentium.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.auth.util.ExtraConstants
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProxyActivity : AppCompatActivity() {
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res -> onSignInResult(res) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { true }
        super.onCreate(savedInstanceState)
        if (Firebase.auth.currentUser == null) {
            if (!checkDeepLink()) authenticate()
        } else navigateHome()
    }

    private fun authenticate() {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName(APPLICATION_ID, true, null)
            .setHandleCodeInApp(true) // This must be set to true
            .setUrl("https://silentiumdynamic.page.link") // This URL needs to be whitelisted
            .build()
        // Choose authentication providers
        val providers = arrayListOf(
            EmailBuilder().setRequireName(false).enableEmailLinkSignIn().setActionCodeSettings(actionCodeSettings).build(),
            PhoneBuilder().build(),
            GoogleBuilder().build()
        )

        // Create and launch sign-in intent
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
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // TODO: show welcome dialog
            navigateHome()
        }
        result.idpResponse != null -> Toast.makeText(this, "Log in error!!", Toast.LENGTH_SHORT).show()
        else -> finish()
    }

    private fun navigateHome(intentFlags: Int = 0) {
        startActivity(Intent(this, SilentActivity::class.java).addFlags(intentFlags))
        overridePendingTransition(0, 0)
        finish()
    }
}
