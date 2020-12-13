package ru.ifmo.setgame

import androidx.lifecycle.*

class GameViewModel(controller: GameController) {
    val boardLiveData: MutableLiveData<MutableList<PlayingCard>> = controller.getBoardLiveData()
    val deckLiveData: MutableLiveData<MutableList<PlayingCard>> = controller.getDeckLiveData()
}