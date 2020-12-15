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

    init {
        screenLiveData.value = GameScreen.NONE
    }

    override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
        screenLiveData.postValue(GameScreen.SCORE_SCREEN)
        navigationDelegate.showScore(title, time, players, scores)
    }

    override fun startMultiplayerGame(gameJson: String) {
        gameController.isMultiplayer = true
        gameController.isComputer = false


        screenLiveData.postValue(GameScreen.BOARD_SCREEN)
        navigationDelegate.showBoardScreen()
    }

    override fun startSingleplayerGame() {
        gameController.isMultiplayer = false
        gameController.isComputer = true

        screenLiveData.postValue(GameScreen.BOARD_SCREEN)
        navigationDelegate.showBoardScreen()
    }

    override fun startTrainingGame() {
        gameController.isMultiplayer = false
        gameController.isComputer = false

        screenLiveData.postValue(GameScreen.BOARD_SCREEN)
        navigationDelegate.showBoardScreen()
    }

    override fun showLobbiesList() {
        screenLiveData.postValue(GameScreen.LOBBIES_SCREEN)
        navigationDelegate.showLobbiesList()
    }

    override fun joinLobby(lobbyId: Int) {
        connector.joinLobby(lobbyId)

        screenLiveData.postValue(GameScreen.LOBBY_INFO_SCREEN)
        navigationDelegate.showLobbyInfoScreen()
    }

    override fun createLobby(maxPlayers: Int) {
        connector.createLobby(maxPlayers)

        screenLiveData.postValue(GameScreen.LOBBY_INFO_SCREEN)
        navigationDelegate.showLobbyInfoScreen()
    }

    interface ScreenNavigation {
        fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray)
        fun showBoardScreen()
        fun showLobbiesList()
        fun showLobbyInfoScreen()
    }
}
