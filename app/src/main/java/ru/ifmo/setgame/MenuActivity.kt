package ru.ifmo.setgame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        btn_multiplayer.setOnClickListener {
            this.startActivity(GameActivity.intentMultiplayer(this))
        }

        btn_singleplayer.setOnClickListener {
            this.startActivity(GameActivity.intentSingleplayer(this))
        }

        btn_training.setOnClickListener {
            this.startActivity(GameActivity.intentTraining(this))
        }

        btn_stats.setOnClickListener{
            this.startActivity(Intent(this, StatsActivity::class.java))
        }

        btn_settings.setOnClickListener{
            this.startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
