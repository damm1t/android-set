package ru.ifmo.setgame.di

import dagger.Module
import dagger.Provides
import ru.ifmo.setgame.GameNavigation
import ru.ifmo.setgame.GameState

@Module
class GameModule {
    @Provides
    fun provideGameNavigation(gameState: GameState): GameNavigation {
        return gameState
    }
}
