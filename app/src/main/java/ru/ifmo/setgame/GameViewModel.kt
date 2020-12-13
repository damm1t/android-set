package ru.ifmo.setgame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel(application : Application,
                    val board : ArrayList<PlayingCard>,
                    val deck : ArrayList<PlayingCard>) : AndroidViewModel(application) {

    val observableBoard = MutableLiveData<MutableList<PlayingCard>>()
    val observableDeck = MutableLiveData<MutableList<PlayingCard>>()

    init {
        setBoard(board)
        setDeck(deck)
    }

    fun getBoard() : MutableLiveData<MutableList<PlayingCard>> {
        return this.observableBoard
    }

    fun getDeck() : MutableLiveData<MutableList<PlayingCard>> {
        return this.observableDeck
    }

    fun setBoard(board: ArrayList<PlayingCard>) {
        observableBoard.value = board
    }

    fun setDeck(deck: ArrayList<PlayingCard>) {
        observableDeck.value = deck
    }

    fun setCard(ind : Int, card: PlayingCard){
        this.observableBoard.value?.set(ind, card)
    }

    fun getCard(ind : Int): PlayingCard? {
        return this.observableBoard.value?.get(ind)
    }
}