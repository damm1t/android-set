package ru.ifmo.setgame

import ru.ifmo.setgame.network.Connector
import ru.ifmo.setgame.di.ActivityScope
import javax.inject.Inject

@ActivityScope
class GameState @Inject constructor(
        private val navigationDelegate: ScreenNavigation,
        private val connector: Connector,
        private val gameController: GameController
): GameNavigation {
    override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
        navigationDelegate.showScore(title, time, players, scores)
    }

    override fun startMultiplayerGame(gameJson: String) {
        gameController.isMultiplayer = true
        gameController.isComputer = false

        navigationDelegate.startMultiplayerGame(gameJson)
    }

    override fun startSingleplayerGame() {
        gameController.isMultiplayer = false
        gameController.isComputer = true

        navigationDelegate.startSingleplayerGame()
    }

    override fun startTrainingGame() {
        gameController.isMultiplayer = false
        gameController.isComputer = false

        navigationDelegate.startTrainingGame()
    }

    override fun showLobbiesList() {
        navigationDelegate.showLobbiesList()
    }

    override fun joinLobby(lobbyId: Int) {
        connector.joinLobby(lobbyId)
        navigationDelegate.joinLobby(lobbyId)
    }

    override fun createLobby(maxPlayers: Int) {
        connector.createLobby(maxPlayers)
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
