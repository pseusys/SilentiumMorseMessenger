package com.ekdorn.silentium.activities

import android.os.Bundle
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ekdorn.silentium.R
import com.ekdorn.silentium.databinding.ActivitySilentRootBinding
import com.ekdorn.silentium.mvs.ContactsViewModel
import com.ekdorn.silentium.mvs.DialogsViewModel
import com.ekdorn.silentium.mvs.MessagesViewModel
import com.ekdorn.silentium.mvs.UserViewModel


class SilentActivity : AppCompatActivity() {
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
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val header = binding.navView.getHeaderView(0)
        userViewModel.me.observe(this) {
            header.findViewById<TextView>(R.id.user_name).text = it.name ?: "[No display name]"
            header.findViewById<TextView>(R.id.user_contact).text = it.contact
        }

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

        // TODO: remove
        ViewModelProvider(this)[DialogsViewModel::class.java].getDialogs(userViewModel.me.value!!)
        ViewModelProvider(this)[ContactsViewModel::class.java].syncContacts()
        ViewModelProvider(this)[MessagesViewModel::class.java].getMessages(userViewModel.me.value!!)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
