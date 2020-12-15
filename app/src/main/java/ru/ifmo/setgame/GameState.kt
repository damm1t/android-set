package ru.ifmo.setgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.ifmo.setgame.network.Connector
import ru.ifmo.setgame.di.ActivityScope
import javax.inject.Inject

enum class GameScreen {
    NONE,
    LOBBIES_SCREEN,
    LOBBY_INFO_SCREEN,
    BOARD_SCREEN,
    SCORE_SCREEN
}

@ActivityScope
class GameState @Inject constructor(
        private val navigationDelegate: ScreenNavigation,
        private val connector: Connector,
        private val gameController: GameController
): GameNavigation {
    private val screenLiveData = MutableLiveData<GameScreen>()

    fun getScreenLiveData(): LiveData<GameScreen> = screenLiveData

    override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
        screenLiveData.value = GameScreen.SCORE_SCREEN
        navigationDelegate.showScore(title, time, players, scores)
    }

    override fun startMultiplayerGame(gameJson: String) {
        gameController.isMultiplayer = true
        gameController.isComputer = false

        screenLiveData.value = GameScreen.SCORE_SCREEN
        navigationDelegate.startMultiplayerGame(gameJson)
    }

    override fun startSingleplayerGame() {
        gameController.isMultiplayer = false
        gameController.isComputer = true

        screenLiveData.value = GameScreen.BOARD_SCREEN
        navigationDelegate.startSingleplayerGame()
    }

    override fun startTrainingGame() {
        gameController.isMultiplayer = false
        gameController.isComputer = false

        screenLiveData.value = GameScreen.BOARD_SCREEN
        navigationDelegate.startTrainingGame()
    }

    override fun showLobbiesList() {
        screenLiveData.value = GameScreen.LOBBIES_SCREEN
        navigationDelegate.showLobbiesList()
    }

    override fun joinLobby(lobbyId: Int) {
        connector.joinLobby(lobbyId)

        screenLiveData.value = GameScreen.LOBBY_INFO_SCREEN
        navigationDelegate.joinLobby(lobbyId)
    }

    override fun createLobby(maxPlayers: Int) {
        connector.createLobby(maxPlayers)

        screenLiveData.value = GameScreen.LOBBY_INFO_SCREEN
        navigationDelegate.createLobby(maxPlayers)
    }

    interface ScreenNavigation {
        fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray)
        fun startMultiplayerGame(gameJson: String)
        fun startSingleplayerGame()
        fun startTrainingGame()
        fun showBoardScreen()
        fun showLobbiesList()
        fun joinLobby(lobbyId: Int)
        fun createLobby(maxPlayers: Int)
    }
}
