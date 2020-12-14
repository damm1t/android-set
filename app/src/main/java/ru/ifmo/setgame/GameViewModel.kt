package ru.ifmo.setgame

import androidx.lifecycle.*

class GameViewModel(controller: GameController) {
    private val boardLiveData: LiveData<MutableList<PlayingCard>> = controller.getBoardLiveData()
    private val deckLiveData: LiveData<MutableList<PlayingCard>> = controller.getDeckLiveData()

    fun getBoard() : LiveData<MutableList<PlayingCard>> {
        return boardLiveData
    }

    fun getDeck() : LiveData<MutableList<PlayingCard>> {
        return deckLiveData
    }
}