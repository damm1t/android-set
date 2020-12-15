package ru.ifmo.setgame

import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import ru.ifmo.setgame.lobby.LobbyInfoViewModel
import ru.ifmo.setgame.network.Connector


@RunWith(RobolectricTestRunner::class)
class LobbyInfoViewModelTest {
    private val liveData = MutableLiveData<String>()

    private val connector = Mockito.mock(Connector::class.java).also {
        Mockito.`when`(it.getLobbiesListLiveData()).thenReturn(liveData)
    }

    private val navigation = Mockito.mock(GameNavigation::class.java)

    private val viewModel = LobbyInfoViewModel(connector, jacksonObjectMapper(), navigation)

    @Test
    fun `lobby data updates`() {
        val lobby = Lobby(98, 2, null, arrayOf())
        val sentLobby = "[{\"lobby_id\":98,\"max_players\":2,\"in_lobby\":[],\"game_id\":null}]"

        liveData.value = sentLobby
        var receivedLobby: Array<Lobby>? = null

        viewModel.lobbiesListLiveData.observeForever { receivedLobby = it }
        Assert.assertNotNull(receivedLobby)
        Assert.assertTrue(receivedLobby!!.isNotEmpty())
        Assert.assertEquals(lobby.lobby_id, receivedLobby!![0].lobby_id)
    }
}
