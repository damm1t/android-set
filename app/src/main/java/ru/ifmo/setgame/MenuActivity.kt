package ru.ifmo.setgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent


class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val button = findViewById<Button>(R.id.button3)
        button.setOnClickListener {
            val myIntent = Intent(this, GameActivity::class.java)
            this.startActivity(myIntent)
        }
    }

}
