package ru.ifmo.setgame.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.ifmo.setgame.GameNavigation
import ru.ifmo.setgame.Lobby
import ru.ifmo.setgame.di.ActivityScope
import ru.ifmo.setgame.network.Connector
import javax.inject.Inject

/**
 * ViewModel with data related to lobbies
 */
// TODO maybe split in two
@ActivityScope
open class LobbyInfoViewModel @Inject constructor(
        private val connector: Connector,
        objectMapper: ObjectMapper,
        private val navigation: GameNavigation
) {
    val lobbiesListLiveData: LiveData<Array<Lobby>> =
            Transformations.map(connector.getLobbiesListLiveData()) { lobbiesListJson ->
                objectMapper.readValue<Array<Lobby>>(lobbiesListJson)
            }
    val lobbyInfoLiveData: LiveData<Lobby> =
            Transformations.map(connector.getLobbyInfoLiveData()) { lobbyJson ->
        objectMapper.readValue<Lobby>(lobbyJson)
    }

    fun joinLobby(lobbyId: Int) {
        navigation.joinLobby(lobbyId)
    }

    fun leaveLobby() {
        // TODO call sth like gameState.leaveLobby()
        connector.leaveLobby()
        navigation.showLobbiesList()
    }

    fun createLobby(maxPlayers: Int) {
        navigation.createLobby(maxPlayers)
    }

    fun refreshLobbiesList() {
        connector.requestLobbies()
    }
}
