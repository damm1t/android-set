package ru.ifmo.setgame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

interface GameInterface {
    fun startGame()
    fun showScore(score: Int)
}

const val SCORE_FRAGMENT_TAG = "SCORE_FRAGMENT_TAG"

class GameActivity : AppCompatActivity(), GameInterface {
    override fun showScore(score: Int) {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameScoreFragment.newInstance(score), SCORE_FRAGMENT_TAG); commit() }
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