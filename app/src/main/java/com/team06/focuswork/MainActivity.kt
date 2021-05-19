package com.team06.focuswork

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.team06.focuswork.model.TasksViewModel
import java.util.*


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var tasksViewModel: TasksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tasksViewModel = ViewModelProvider(this).get(TasksViewModel::class.java)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_overview, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Load default values for settings in case user hasn't selected values yet
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        checkLocale()

        // set listener for settings
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        //Unregister settings listener
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onDestroy()
    }

    /**
     * If the preferred language is not the current language,
     * restarts the activity with the preferred language
     */
    @Suppress("DEPRECATION")
    private fun checkLocale() {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val languageKey = (preferences.getString("language", "en")).toString()
        if (languageKey.toLowerCase(Locale.getDefault()) != resources.configuration.locale.language.toLowerCase(
                Locale.getDefault()
            )
        )
            onChangedLanguage(languageKey)
    }

    @Suppress("DEPRECATION")
    private fun onChangedLanguage(languageKey: String) {
        val myLocale = Locale(languageKey)
        val dm: DisplayMetrics = resources.displayMetrics
        val conf: Configuration = resources.configuration
        conf.locale = myLocale
        resources.updateConfiguration(conf, dm)

        finish()
        startActivity(this.intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "language") {
            val languageValue: String = (sharedPreferences?.getString(key, "en")).toString()
            onChangedLanguage(languageValue);
        }
    }

}