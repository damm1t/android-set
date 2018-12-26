package ru.ifmo.setgame

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_settings.*

const val PREFERENCES_NAME = "game_prefs"
const val PREFERENCE_CUSTOM_CARDS = "CUSTOM_CARDS"

class SettingsActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                preferences.edit().putBoolean(PREFERENCE_CUSTOM_CARDS, true).apply()
            } else {
                switch_custom_cards.isChecked = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        btn_reset_stats.setOnClickListener {
            preferences.edit()
                    .putInt(STAT_TOTAL_GAMES, 0)
                    .putLong(STAT_TOTAL_TIME, 0)
                    .putInt(STAT_TOTAL_SCORE, 0)
                    .apply()
        }

        switch_custom_cards.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                } else {
                    preferences.edit().putBoolean(PREFERENCE_CUSTOM_CARDS, true).apply()
                }
            } else {
                preferences.edit().putBoolean(PREFERENCE_CUSTOM_CARDS, false).apply()
            }
        }


        switch_custom_cards.isChecked = preferences.getBoolean(PREFERENCE_CUSTOM_CARDS, false)

    }
}
