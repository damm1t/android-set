package ru.ifmo.setgame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.ifmo.setgame.lobby.LobbyInfoFragment
import ru.ifmo.setgame.lobby.LobbySelectionFragment
import ru.ifmo.setgame.di.DaggerGameComponent
import ru.ifmo.setgame.di.GameComponent

class Lobby(
        val lobby_id: Int,
        val max_players: Int,
        val game_id: Int?,
        val in_lobby: Array<Int>
)

class GameActivity : AppCompatActivity() {
    val gameNavigation: GameNavigation = GameNavigationImpl()
    lateinit var gameComponent: GameComponent

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

        gameComponent = DaggerGameComponent.builder()
                .setContext(this)
                .setGameNavigation(gameNavigation)
                .setObjectMapper(jacksonObjectMapper())
                .build()

        setContentView(R.layout.activity_game)

        intent.extras?.apply {
            val isMultiplayer = getBoolean(INTENT_KEY_MULTIPLAYER)
            val isComputer = getBoolean(INTENT_KEY_SINGLEPLAYER)

            when {
                isMultiplayer -> {
                    connector = gameComponent.connector()
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
            setFragment(GameScoreFragment.newInstance(title, time, players, scores))
        }

        override fun startMultiplayerGame(gameJson: String) {
            setFragment(GameFragment.newInstance(gameJson, true, false))
        }

        override fun startSingleplayerGame() {
            setFragment(GameFragment.newInstance("", false, true))
        }

        override fun startTrainingGame() {
            setFragment(GameFragment.newInstance("", false, false))
        }

        override fun showLobbiesList() {
            setFragment(LobbySelectionFragment())
        }

        override fun joinLobby(lobbyId: Int) {
            connector.joinLobby(lobbyId)
            setFragment(LobbyInfoFragment())
        }

        override fun createLobby(maxPlayers: Int) {
            connector.createLobby(maxPlayers)
            setFragment(LobbyInfoFragment())
        }

        private fun setFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.game_fragment, fragment)
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