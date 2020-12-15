package ru.ifmo.setgame.di

import android.content.Context
import ru.ifmo.setgame.GameActivity

class ComponentHelper {
    companion object {
        fun getGameComponent(context: Context): GameComponent {
            return (context as GameActivity).gameComponent
        }
    }
}
