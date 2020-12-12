package ru.ifmo.setgame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class Lobby(
        val lobby_id: Int,
        val max_players: Int,
        val game_id: Int?,
        val in_lobby: Array<Int>
)

class GameActivity : AppCompatActivity() {
    val gameNavigation: GameNavigation = GameNavigationImpl()

    lateinit var connector : Connector
        private set

    override fun onStart() {
        super.onStart()
        if (::connector.isInitialized) {
            connector.gameNavigation = gameNavigation
        }
    }

    override fun onStop() {
        if (::connector.isInitialized) {
            connector.gameNavigation = null
        }
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
                    connector = Connector(this@GameActivity)
                    connector.connect()

                    gameNavigation.showLobbiesList()
                }
                isComputer -> gameNavigation.startSingleplayerGame()
                else -> gameNavigation.startTrainingGame()
            }
        }
    }

    override fun onDestroy() {
        if (::connector.isInitialized) {
            connector.close()
        }
        super.onDestroy()
    }

    private inner class GameNavigationImpl: GameNavigation {
        override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.game_fragment, GameScoreFragment.newInstance(title, time, players, scores))
                commit()
            }
        }

        override fun startMultiplayerGame(gameJson: String) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.game_fragment, GameFragment.newInstance(gameJson, true, false))
                commit()
            }
        }

        override fun startSingleplayerGame() {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.game_fragment, GameFragment.newInstance("", false, true))
                commit()
            }
        }

        override fun startTrainingGame() {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.game_fragment, GameFragment.newInstance("", false, false))
                commit()
            }
        }

        override fun showLobbiesList() {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.game_fragment, LobbySelectionFragment());
                commit()
            }
        }
    }

    companion object {
        private const val INTENT_KEY_MULTIPLAYER = "multiplayer"
        private const val INTENT_KEY_SINGLEPLAYER = "singleplayer"

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
    }
}