package ru.ifmo.setgame.score

import ru.ifmo.setgame.GameState
import javax.inject.Inject

class ScoreViewModel @Inject constructor(private val gameController: GameState) {
    fun getScoreLiveData() = gameController.getScoreLiveData()
}
