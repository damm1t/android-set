package ru.ifmo.setgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.ifmo.setgame.network.Connector
import ru.ifmo.setgame.di.ActivityScope
import ru.ifmo.setgame.score.ScoreInfo
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
        private val connector: Connector,
        private val gameController: GameController
): GameNavigation {
    private val screenLiveData = MutableLiveData<GameScreen>()
    private val scoreLiveData = MutableLiveData<ScoreInfo>()

    fun getScreenLiveData(): LiveData<GameScreen> = screenLiveData
    fun getScoreLiveData(): LiveData<ScoreInfo> = scoreLiveData

    init {
        screenLiveData.value = GameScreen.NONE
    }

    override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
        scoreLiveData.postValue(ScoreInfo(
                title, time, players, scores
        ))
        screenLiveData.postValue(GameScreen.SCORE_SCREEN)
    }

    override fun startMultiplayerGame(gameJson: String) {
        gameController.isMultiplayer = true
        gameController.isComputer = false


        screenLiveData.postValue(GameScreen.BOARD_SCREEN)
    }

    override fun startSingleplayerGame() {
        gameController.isMultiplayer = false
        gameController.isComputer = true

        screenLiveData.postValue(GameScreen.BOARD_SCREEN)
    }

    override fun startTrainingGame() {
        gameController.isMultiplayer = false
        gameController.isComputer = false

        screenLiveData.postValue(GameScreen.BOARD_SCREEN)
    }

    override fun showLobbiesList() {
        screenLiveData.postValue(GameScreen.LOBBIES_SCREEN)
    }

    override fun joinLobby(lobbyId: Int) {
        connector.joinLobby(lobbyId)

        screenLiveData.postValue(GameScreen.LOBBY_INFO_SCREEN)
    }

    override fun createLobby(maxPlayers: Int) {
        connector.createLobby(maxPlayers)

        screenLiveData.postValue(GameScreen.LOBBY_INFO_SCREEN)
    }
}
