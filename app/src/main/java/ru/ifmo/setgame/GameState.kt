package ru.ifmo.setgame

import ru.ifmo.setgame.network.Connector
import javax.inject.Inject

class GameState @Inject constructor(
        private val navigationDelegate: ScreenNavigation,
        private val connector: Connector
): GameNavigation {
    override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
        navigationDelegate.showScore(title, time, players, scores)
    }

    override fun startMultiplayerGame(gameJson: String) {
        navigationDelegate.startMultiplayerGame(gameJson)
    }

    override fun startSingleplayerGame() {
        navigationDelegate.startSingleplayerGame()
    }

    override fun startTrainingGame() {
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
        fun showLobbiesList()
        fun joinLobby(lobbyId: Int)
        fun createLobby(maxPlayers: Int)
    }
}
