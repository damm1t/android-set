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
            val myIntent = Intent(this, GameActivity::class.java).apply {
                putExtra("multiplayer", true)
                putExtra("computer", false)
            }

            this.startActivity(myIntent)
        }

        btn_singleplayer.setOnClickListener {
            val myIntent = Intent(this, GameActivity::class.java).apply {
                putExtra("multiplayer", false)
                putExtra("computer", true)
            }

            this.startActivity(myIntent)
        }

        btn_training.setOnClickListener {
            val myIntent = Intent(this, GameActivity::class.java).apply {
                putExtra("multiplayer", false)
                putExtra("computer", false)
            }

            this.startActivity(myIntent)
        }

        btn_stats.setOnClickListener{
            this.startActivity(Intent(this, StatsActivity::class.java))
        }

        btn_settings.setOnClickListener{
            this.startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
