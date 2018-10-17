package ru.ifmo.setgame

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameFragment()); commit() }
    }
}