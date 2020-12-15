package ru.ifmo.setgame.di

import dagger.Module
import dagger.Provides
import ru.ifmo.setgame.Connector

@Module
class NetworkModule {
    @Provides
    @ActivityScope
    fun provideConnector(): Connector {
        return Connector.createConnector()
    }
}
