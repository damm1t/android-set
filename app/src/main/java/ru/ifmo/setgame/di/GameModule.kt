package ru.ifmo.setgame.di

import dagger.Module
import dagger.Provides
import ru.ifmo.setgame.GameController
import ru.ifmo.setgame.GameNavigation
import ru.ifmo.setgame.GameState

@Module
class GameModule {
    @Provides
    @ActivityScope
    fun provideGameNavigation(gameState: GameState): GameNavigation {
        return gameState
    }

    @Provides
    @ActivityScope
    fun provideGameController(): GameController {
        return GameController()
    }
}
