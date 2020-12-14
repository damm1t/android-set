package ru.ifmo.setgame.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.ifmo.setgame.Connector

@Module
class NetworkModule {
    @Provides
    fun provideConnector(): Connector {
        return Connector.createConnector()
    }
}
