package ru.ifmo.setgame.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.ifmo.setgame.Connector
import ru.ifmo.setgame.Lobby
import java.util.*

/**
 * ViewModel with data related to lobbies
 */
// TODO maybe split in two
class LobbyInfoViewModel(connector: Connector, objectMapper: ObjectMapper) {
    val lobbiesListLiveData: LiveData<Array<Lobby>> =
            Transformations.map(connector.getLobbiesListLiveData()) { lobbiesListJson ->
        objectMapper.readValue<Array<Lobby>>(lobbiesListJson)
    }
    val lobbyInfoLiveData: LiveData<Lobby> =
            Transformations.map(connector.getLobbyInfoLiveData()) { lobbyJson ->
        objectMapper.readValue<Lobby>(lobbyJson)
    }
}
