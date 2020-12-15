package ru.ifmo.setgame

class GameState(private val navigationDelegate: ScreenNavigation): GameNavigation {
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
        navigationDelegate.joinLobby(lobbyId)
    }

    override fun createLobby(maxPlayers: Int) {
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