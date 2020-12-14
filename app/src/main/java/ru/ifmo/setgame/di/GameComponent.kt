package ru.ifmo.setgame.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.ifmo.setgame.Connector
import javax.inject.Scope

@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope

@Component(modules = [NetworkModule::class])
@ActivityScope
interface GameComponent {
    fun connector(): Connector

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun setContext(context: Context): Builder

        fun build(): GameComponent
    }
}
