package com.ekdorn.silentium.activities

import android.os.Bundle
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.ekdorn.silentium.R
import com.ekdorn.silentium.databinding.ActivitySilentRootBinding
import com.ekdorn.silentium.fragments.InputFragmentDirections
import com.ekdorn.silentium.fragments.MessagesFragment
import com.ekdorn.silentium.managers.UserManager


class SilentActivity : AppCompatActivity() {
    companion object {
        const val NAVIGATE_TO_SETTINGS = "settings_call"
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

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_notes,
            R.id.nav_input,
            R.id.nav_dialogs,
            R.id.nav_contacts,
            R.id.nav_description,
            R.id.nav_settings
        ), binding.drawerLayout)

        if (intent.hasExtra(NAVIGATE_TO_SETTINGS)) {
            val payload = intent.getStringExtra(NAVIGATE_TO_SETTINGS)!!
            val action = InputFragmentDirections.actionNavInputToNavSettings(payload)
            navController.navigate(action)
        } else {
            setupActionBarWithNavController(navController, appBarConfiguration)

            val header = binding.navView.getHeaderView(0)
            UserManager[this].observe(this) {
                header.findViewById<TextView>(R.id.user_name).text = it.name ?: "[No display name]"
                header.findViewById<TextView>(R.id.user_contact).text = it.contact
            }
        }

        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.get(0) as? MessagesFragment
        if ((currentFragment == null) || (!currentFragment.onBackPressed())) super.onBackPressed()
    }
}
