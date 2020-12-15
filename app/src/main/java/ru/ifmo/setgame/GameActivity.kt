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
import ru.ifmo.setgame.network.Connector

class Lobby(
        val lobby_id: Int,
        val max_players: Int,
        val game_id: Int?,
        val in_lobby: Array<Int>
)

class GameActivity : AppCompatActivity() {
    private val screenNavigation: GameState.ScreenNavigation = ScreenNavigationImpl()
    lateinit var gameComponent: GameComponent

    lateinit var connector : Connector
        private set

    private lateinit var gameState: GameState

    override fun onStart() {
        super.onStart()
        if (::connector.isInitialized) {
            connector.gameNavigation = gameState
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
                .setScreenNavigation(screenNavigation)
                .setObjectMapper(jacksonObjectMapper())
                .build()

        gameState = gameComponent.gameState()

        setContentView(R.layout.activity_game)

        intent.extras?.apply {
            val isMultiplayer = getBoolean(INTENT_KEY_MULTIPLAYER)
            val isComputer = getBoolean(INTENT_KEY_SINGLEPLAYER)

            when {
                isMultiplayer -> {
                    connector = gameComponent.connector()
                    connector.connect()

                    gameState.showLobbiesList()
                }
                isComputer -> gameState.startSingleplayerGame()
                else -> gameState.startTrainingGame()
            }
        }

        gameState.getScreenLiveData().observe(this, ::showScreen)
    }

    override fun onDestroy() {
        if (::connector.isInitialized) {
            connector.close()
        }
        super.onDestroy()
    }

    private fun showScreen(screen: GameScreen) {
        when (screen) {
            GameScreen.NONE -> Unit // no-op
            GameScreen.LOBBIES_SCREEN -> screenNavigation.showLobbiesList()
            GameScreen.LOBBY_INFO_SCREEN -> screenNavigation.showLobbyInfoScreen()
            GameScreen.BOARD_SCREEN -> screenNavigation.showBoardScreen()
            GameScreen.SCORE_SCREEN -> Unit // no-op
        }
    }

    private inner class ScreenNavigationImpl: GameState.ScreenNavigation {
        override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
            setFragment(GameScoreFragment.newInstance(title, time, players, scores))
        }

        override fun showBoardScreen() {
            setFragment(GameFragment.newInstance())
        }

        override fun showLobbiesList() {
            setFragment(LobbySelectionFragment())
        }

        override fun showLobbyInfoScreen() {
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