package ru.ifmo.setgame.di

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.BindsInstance
import dagger.Component
import ru.ifmo.setgame.Connector
import ru.ifmo.setgame.GameNavigation
import ru.ifmo.setgame.lobby.LobbyInfoViewModel
import javax.inject.Scope

@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope

@Component(modules = [NetworkModule::class])
@ActivityScope
interface GameComponent {
    fun connector(): Connector

    fun lobbyInfoViewModel(): LobbyInfoViewModel

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun setContext(context: Context): Builder

        @BindsInstance
        fun setObjectMapper(objectMapper: ObjectMapper): Builder

        @BindsInstance
        fun setGameNavigation(gameNavigation: GameNavigation): Builder

        fun build(): GameComponent
    }
}
