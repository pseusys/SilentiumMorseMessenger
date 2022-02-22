package com.ekdorn.silentium.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.ekdorn.silentium.R
import com.ekdorn.silentium.databinding.ActivitySilentRootBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SilentActivity : AppCompatActivity() {
    companion object {
        const val PREFERENCES_FILE = "settings"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySilentRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySilentRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.wrapperMain.toolbar)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val user = Firebase.auth.currentUser!!
        val contact = if (!user.phoneNumber.isNullOrBlank()) user.phoneNumber
        else if (!user.email.isNullOrBlank()) user.email
        else "Unknown sign in method used"

        val header = binding.navView.getHeaderView(0)
        header.findViewById<TextView>(R.id.user_name).text = user.displayName ?: "[No display name]"
        header.findViewById<TextView>(R.id.user_contact).text = contact

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_notes,
            R.id.nav_input,
            R.id.nav_dialogs,
            R.id.nav_contacts,
            R.id.nav_description,
            R.id.nav_settings
        ), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
