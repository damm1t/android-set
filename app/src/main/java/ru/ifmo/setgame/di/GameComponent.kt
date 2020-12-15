package ru.ifmo.setgame.di

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.BindsInstance
import dagger.Component
import ru.ifmo.setgame.network.Connector
import ru.ifmo.setgame.GameController
import ru.ifmo.setgame.GameNavigation
import ru.ifmo.setgame.GameState
import ru.ifmo.setgame.GameViewModel
import ru.ifmo.setgame.lobby.LobbyInfoViewModel
import ru.ifmo.setgame.network.NetworkModule
import ru.ifmo.setgame.score.ScoreViewModel
import javax.inject.Scope

@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope

@Component(modules = [NetworkModule::class, GameModule::class])
@ActivityScope
interface GameComponent {
    fun connector(): Connector

    fun lobbyInfoViewModel(): LobbyInfoViewModel

    fun gameController(): GameController

    fun gameViewModel(): GameViewModel

    fun gameState(): GameState

    fun gameNavigation(): GameNavigation

    fun scoreViewModel(): ScoreViewModel

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun setContext(context: Context): Builder

        @BindsInstance
        fun setObjectMapper(objectMapper: ObjectMapper): Builder

        @BindsInstance
        fun setScreenNavigation(screenNavigation: GameState.ScreenNavigation): Builder

        fun build(): GameComponent
    }
}
