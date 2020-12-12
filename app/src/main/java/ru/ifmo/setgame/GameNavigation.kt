package ru.ifmo.setgame

interface GameNavigation {
    fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray)
    fun startMultiplayerGame(json: String)
    fun startSingleplayerGame()
    fun startTrainingGame()
    fun showLobbiesList()
}