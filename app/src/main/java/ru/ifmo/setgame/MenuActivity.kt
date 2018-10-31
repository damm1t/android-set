package ru.ifmo.setgame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.support.v4.content.ContextCompat.startActivity
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
