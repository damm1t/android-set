package ru.ifmo.setgame.network

import dagger.Module
import dagger.Provides
import ru.ifmo.setgame.di.ActivityScope

@Module
class NetworkModule {
    @Provides
    @ActivityScope
    fun provideConnector(): Connector {
        return Connector.createConnector()
    }
}
