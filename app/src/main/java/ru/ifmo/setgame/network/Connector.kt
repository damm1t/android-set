package ru.ifmo.setgame.network

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.ifmo.setgame.GameNavigation
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.coroutines.CoroutineContext

open class Connector @VisibleForTesting constructor(
        private val socket: Socket,
        private val coroutineScope: CoroutineScope
) : AutoCloseable {

    private val mutex = Mutex()
    private lateinit var reader: BufferedReader // = BufferedReader(InputStreamReader(socket.getInputStream()))
    private lateinit var writer: BufferedWriter // = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), "UTF-8"))
    private val mapper = jacksonObjectMapper()

    var gameNavigation: GameNavigation? = null

    private val lobbiesListLiveData = MutableLiveData<String>()
    private val lobbyInfoLiveData = MutableLiveData<String>()
    private val gameLiveData = MutableLiveData<String>()

    private var playerId = -1
    private var lobbyId = -1
    private var gameId = -1
    private var status = "NEW"

    open fun getLobbiesListLiveData(): LiveData<String> = lobbiesListLiveData
    fun getLobbyInfoLiveData(): LiveData<String> = lobbyInfoLiveData
    fun getGameLiveData(): LiveData<String> = gameLiveData

    fun requestLobbies() = coroutineScope.launch {
        mutex.withLock {
            val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "refresh_list"}""".trimMargin()

            sendString(request)
        }
    }

    fun createLobby(maxPlayers: Int) = coroutineScope.launch {
        mutex.withLock {
            val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "create_lobby",
            |"max_players": $maxPlayers}""".trimMargin()

            sendString(request)
        }
    }

    fun joinLobby(mLobbyId: Int) = coroutineScope.launch {
        mutex.withLock {
            val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "join_lobby",
            |"lobby_id": $mLobbyId}""".trimMargin()

            sendString(request)
        }
    }

    fun leaveLobby() = coroutineScope.launch {
        mutex.withLock {
            val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "leave_lobby",
            |"lobby_id": $lobbyId}""".trimMargin()

            sendString(request)
        }
    }

    fun make_move(positions: IntArray) = coroutineScope.launch {
        mutex.withLock {
            val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "make_move",
            |"lobby_id": $lobbyId,
            |"game_id": $gameId,
            |"move_positions": ${mapper.writeValueAsString(positions)}}""".trimMargin()

            sendString(request)
        }
    }

    // send default handshake and get player id and list of lobbies
    private suspend fun init() = mutex.withLock {
        val request = """{"status": "$status"}"""

        sendString(request)
    }

    fun connect() = coroutineScope.launch {
        socket.connect(InetSocketAddress(HOST_ADDRESS, HOST_PORT))
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), "UTF-8"))

        init()

        while (socket.isConnected) {
            if (reader.ready()) {
                val response = receiveJson()

                if (status == "NEW") {
                    playerId = response.get("player_id").asInt()
                    status = response.get("status").asText()
                    val lobbiesListJson = mapper.writeValueAsString(response.get("lobbies_list"))
                    lobbiesListLiveData.postValue(lobbiesListJson)
                }
                if (status == "SELECTING_LOBBY") {
                    status = response.get("status").asText()

                    if (status == "IN_LOBBY") {
                        // moved into lobby
                        lobbyId = response.get("lobby_id").asInt()

                        val lobbyInfoJson = mapper.writeValueAsString(response.get("lobby"))
                        lobbyInfoLiveData.postValue(lobbyInfoJson)
                    } else if (status == "SELECTING_LOBBY") {
                        // make sure we show lobbies list
                        gameNavigation?.showLobbiesList()

                        val lobbiesListJson = mapper.writeValueAsString(response.get("lobbies_list"))
                        lobbiesListLiveData.postValue(lobbiesListJson)
                    }
                } else if (status == "IN_LOBBY") {
                    status = response.get("status").asText()

                    if (status == "IN_GAME") {
                        gameId = response.get("game_id").asInt()
                        val gameJson = mapper.writeValueAsString(response.get("game"))
                        gameNavigation?.startMultiplayerGame(gameJson)
                    } else if (status == "IN_LOBBY") {
                        val lobbyInfoJson = mapper.writeValueAsString(response.get("lobby"))
                        lobbyInfoLiveData.postValue(lobbyInfoJson)
                    } else {
                        Log.d("Connector", "received $response")
                        lobbyId = -1

                        assert(status == "SELECTING_LOBBY")

                        val lobbiesListJson = mapper.writeValueAsString(response.get("lobbies_list"))
                        lobbiesListLiveData.postValue(lobbiesListJson)
                    }
                } else if (status == "IN_GAME") {
                    status = response.get("status").asText()

                    if (status == "GAME_ENDED") {
                        val trscores = response.get("score")

                        val players = mutableListOf<String>()
                        val scores = mutableListOf<Int>()

                        for (pr in trscores.fields()) {
                            if (pr.key == playerId.toString()) {
                                players.add("You")
                            } else {
                                players.add("Player #${pr.key}")
                            }
                            scores.add(pr.value.asInt())
                        }

                        gameNavigation?.showScore(
                                "Game #$lobbyId results",
                                response.get("time").asLong(),
                                players.toTypedArray(),
                                scores.toIntArray()
                        )
                        break
                    } else {
                        val gameJson = mapper.writeValueAsString(response.get("game"))
                        gameLiveData.postValue(gameJson)
                    }
                }
            }
        }
    }

    private fun receiveJson(): JsonNode {
        val response = reader.readLine()
        Log.d("Connector", "received $response")

        return mapper.readTree(response)
    }

    private fun sendString(request: String) {
        Log.d("Connector", "sending $request")

        writer.write(request)
        writer.flush()
    }

    fun ready() = reader.ready()

    override fun close() {
        socket.close()
        coroutineScope.coroutineContext.job.cancelChildren()
    }

    companion object {
        private const val HOST_ADDRESS = "rsbat.dev"
        private const val HOST_PORT = 3691

        fun createConnector(): Connector {
            val socket = Socket()

            val job = SupervisorJob()

            val coroutineContext: CoroutineContext = Dispatchers.IO + job

            return Connector(socket, CoroutineScope(coroutineContext))
        }
    }
}
