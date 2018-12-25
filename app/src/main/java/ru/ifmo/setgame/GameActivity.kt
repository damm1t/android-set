package ru.ifmo.setgame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface GameInterface {
    fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray)
    fun startMultiplayerGame(json: String)
    fun startSingleplayerGame()
    fun startTrainingGame()
}

class Lobby(
        val lobby_id: Int,
        val max_players: Int,
        val game_id: Int?,
        val in_lobby: Array<Int>
)

const val SCORE_FRAGMENT_TAG = "SCORE_FRAGMENT_TAG"


class GameActivity : AppCompatActivity(), GameInterface {
    lateinit var connector: Connector
        private set

    val goToScoreReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val extras = intent?.extras!!

            val title = extras.getString("TITLE_TAG")!!
            val time = extras.getLong("TIME_TAG")
            val players = extras.getStringArray("PLAYERS_TAG")!!
            val scores = extras.getIntArray("SCORES_TAG")!!

            showScore(title, time, players, scores)
        }
    }

    val goToGameReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val gameStr = intent?.extras?.getString("game")!!
            startMultiplayerGame(gameStr)
        }
    }

    override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameScoreFragment.newInstance(title, time, players, scores), SCORE_FRAGMENT_TAG); commit() }
    }

    override fun startMultiplayerGame(json: String) {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameFragment.newInstance(json, true, false)); commit() }
    }

    override fun startSingleplayerGame() {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameFragment.newInstance("", false, true)); commit() }
    }

    override fun startTrainingGame() {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameFragment.newInstance("", false, false)); commit() }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(goToGameReceiver, IntentFilter(TO_GAME))
        LocalBroadcastManager.getInstance(this).registerReceiver(goToScoreReceiver, IntentFilter(TO_GAME))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(goToGameReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(goToScoreReceiver)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        intent.extras?.apply {
            val isMultiplayer = getBoolean("multiplayer")
            val isComputer = getBoolean("computer")

            when {
                isMultiplayer -> {
                    GlobalScope.launch {
                        connector = Connector(this@GameActivity)
                        connector.connect()
                        connector.close()
                    }

                    supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, LobbySelectionFragment()); commit() }
                }
                isComputer -> startSingleplayerGame()
                else -> startTrainingGame()
            }
        }
    }
}