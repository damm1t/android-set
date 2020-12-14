package ru.ifmo.setgame

import androidx.lifecycle.*

class GameViewModel(controller: GameController) {
    private val boardLiveData: LiveData<MutableList<PlayingCard>> = controller.getBoardLiveData()

    fun getBoard() : LiveData<MutableList<PlayingCard>> {
        return boardLiveData
    }
}