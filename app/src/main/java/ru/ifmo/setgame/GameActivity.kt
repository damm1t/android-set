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
    fun showLobbiesList()
}

class Lobby(
        val lobby_id: Int,
        val max_players: Int,
        val game_id: Int?,
        val in_lobby: Array<Int>
)

class GameActivity : AppCompatActivity(), GameInterface {

    lateinit var connector: Connector
        private set

    val goToScoreReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val extras = intent?.extras!!

            val title = extras.getString(INTENT_KEY_TITLE)!!
            val time = extras.getLong(INTENT_KEY_TIME)
            val players = extras.getStringArray(INTENT_KEY_PLAYERS)!!
            val scores = extras.getIntArray(INTENT_KEY_SCORES)!!

            showScore(title, time, players, scores)
        }
    }

    val goToGameReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val gameStr = intent?.extras?.getString("game")!!
            startMultiplayerGame(gameStr)
        }
    }

    val goToLobbiesReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            showLobbiesList()
        }
    }

    override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameScoreFragment.newInstance(title, time, players, scores)); commit() }
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

    override fun showLobbiesList() {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, LobbySelectionFragment()); commit() }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(goToGameReceiver, IntentFilter(TO_GAME))
        LocalBroadcastManager.getInstance(this).registerReceiver(goToScoreReceiver, IntentFilter(TO_SCORE))
        LocalBroadcastManager.getInstance(this).registerReceiver(goToLobbiesReceiver, IntentFilter(TO_LOBBIES))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(goToGameReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(goToScoreReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(goToLobbiesReceiver)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        intent.extras?.apply {
            val isMultiplayer = getBoolean(INTENT_KEY_MULTIPLAYER)
            val isComputer = getBoolean(INTENT_KEY_SINGLEPLAYER)

            when {
                isMultiplayer -> {
                    GlobalScope.launch {
                        connector = Connector(this@GameActivity)
                        connector.connect()
                        connector.close()
                    }

                    showLobbiesList()
                }
                isComputer -> startSingleplayerGame()
                else -> startTrainingGame()
            }
        }
    }

    companion object {
        private const val INTENT_KEY_MULTIPLAYER = "multiplayer"
        private const val INTENT_KEY_SINGLEPLAYER = "singleplayer"
        private const val INTENT_KEY_TITLE = "title"
        private const val INTENT_KEY_TIME = "time"
        private const val INTENT_KEY_PLAYERS = "players"
        private const val INTENT_KEY_SCORES = "scores"

        @JvmStatic
        fun intentMultiplayer(context: Context) : Intent = Intent(context, GameActivity::class.java).apply {
            putExtra(INTENT_KEY_MULTIPLAYER, true)
            putExtra(INTENT_KEY_SINGLEPLAYER, false)
        }

        @JvmStatic
        fun intentSingleplayer(context: Context) : Intent = Intent(context, GameActivity::class.java).apply {
            putExtra(INTENT_KEY_MULTIPLAYER, false)
            putExtra(INTENT_KEY_SINGLEPLAYER, true)
        }

        @JvmStatic
        fun intentTraining(context: Context) : Intent = Intent(context, GameActivity::class.java).apply {
            putExtra(INTENT_KEY_MULTIPLAYER, false)
            putExtra(INTENT_KEY_SINGLEPLAYER, false)
        }

        fun intentScore(title: String, time: Long, players: Array<String>, scores: IntArray) :Intent = Intent(TO_SCORE).apply {
            putExtra(INTENT_KEY_TITLE, title)
            putExtra(INTENT_KEY_TIME, time)
            putExtra(INTENT_KEY_PLAYERS, players)
            putExtra(INTENT_KEY_SCORES, scores)
        }
    }
}