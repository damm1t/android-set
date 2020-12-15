package ru.ifmo.setgame

import androidx.lifecycle.*
import ru.ifmo.setgame.di.ActivityScope
import javax.inject.Inject

@ActivityScope
class GameViewModel @Inject constructor(controller: GameController) {
    private val boardLiveData: LiveData<List<PlayingCard>> =
            Transformations.map(controller.getBoardLiveData()) { it }

    fun getBoard() : LiveData<List<PlayingCard>> {
        return boardLiveData
    }
}
