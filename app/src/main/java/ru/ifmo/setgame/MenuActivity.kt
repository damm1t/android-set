package ru.ifmo.setgame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        button1.setOnClickListener {
            val myIntent = Intent(this, MultiplayerGameActivity::class.java)
            this.startActivity(myIntent)
        }
    }

}
