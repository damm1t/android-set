package ru.ifmo.setgame

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

interface GameInterface {
    fun startGame()
    fun showScore(score : Int)
}

class GameActivity : AppCompatActivity(), GameInterface {
    override fun showScore(score : Int) {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameScoreFragment.newInstance(score)); commit() }
    }

    override fun startGame() {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameFragment()); commit() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameCreationFragment()); commit() }
    }
}