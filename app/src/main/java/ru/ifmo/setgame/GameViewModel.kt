package ru.ifmo.setgame

import androidx.lifecycle.*

class GameViewModel(controller: GameController) {
    private val boardLiveData: LiveData<List<PlayingCard>> =
            Transformations.map(controller.getBoardLiveData()) { it }

    fun getBoard() : LiveData<List<PlayingCard>> {
        return boardLiveData
    }
}