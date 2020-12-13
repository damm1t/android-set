package ru.ifmo.setgame

import androidx.lifecycle.*

class GameViewModel(controller: GameController) {
    val boardLiveData: LiveData<List<PlayingCard>> =
            Transformations.map(controller.getBoardLiveData()) { board ->
                board
            }
    val deckLiveData: LiveData<List<PlayingCard>> =
            Transformations.map(controller.getDeckLiveData()) { deck ->
               deck
            }
}