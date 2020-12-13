package ru.ifmo.setgame

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(val application: Application,
                           val board : ArrayList<PlayingCard>,
                           val deck : ArrayList<PlayingCard>) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameViewModel(application, board, deck) as T
    }
}