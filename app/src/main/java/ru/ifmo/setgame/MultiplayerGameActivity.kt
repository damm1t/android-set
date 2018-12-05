package ru.ifmo.setgame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

interface GameInterface {

    fun startGame()
    fun showScore(score: Int)
}

class Lobby(
        val lobby_id: Int,
        val max_players: Int,
        val game_id: Int?,
        val in_lobby: Array<Int>
)

const val SCORE_FRAGMENT_TAG = "SCORE_FRAGMENT_TAG"

class Connector(context: Context) : AutoCloseable {
    val LOBBIES_LIST_BROADCAST = "ru.ifmo.setgame.LOBBIES_LIST"
    val IN_LOBBY_BROADCAST = "ru.ifmo.setgame.IN_LOBBY"
    val IN_GAME_BROADCAST = "ru.ifmo.setgame.IN_GAME"

    private val mutex = Mutex()
    private val hostAddress = "18.222.225.249"
    private val hostPort = 3691
    private val socket = Socket(hostAddress, hostPort)
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), "UTF-8"))
    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)
    private val mapper = jacksonObjectMapper()

    private var playerId = -1
    private var lobbyId = -1
    private var gameId = -1
    private var status = "NEW"

    fun requestLobbies() = GlobalScope.launch { mutex.withLock {
        val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "refresh_list"}""".trimMargin()

        writer.write(request)
        writer.flush()

        val response = mapper.readTree(reader.readLine())
        status = response.get("status").asText()
        val lobbiesStr = mapper.writeValueAsString(response.get("lobbies_list"))

        localBroadcastManager.sendBroadcast(Intent(LOBBIES_LIST_BROADCAST).apply { putExtra("lobbies_list", lobbiesStr) })
    }}

    fun createLobby(maxPlayers: Int) = GlobalScope.launch { mutex.withLock {
        val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "create_lobby",
            |"max_players": $maxPlayers}""".trimMargin()

        writer.write(request)
        writer.flush()

        val response = mapper.readTree(reader.readLine())
        status = response.get("status").asText()
        val lobbyStr = mapper.writeValueAsString(response.get("lobby"))

        localBroadcastManager.sendBroadcast(Intent(IN_LOBBY_BROADCAST).apply { putExtra("lobby", lobbyStr) })
    } }

    fun joinLobby(mLobbyId : Int) = GlobalScope.launch { mutex.withLock {
        val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "join_lobby",
            |"lobby_id": $mLobbyId}""".trimMargin()

        writer.write(request)
        writer.flush()

        val response = mapper.readTree(reader.readLine())
        status = response.get("status").asText()
        lobbyId = response.get("lobby_id").asInt()

        if (status != "IN_LOBBY") TODO()

        val lobbyStr = mapper.writeValueAsString(response.get("lobby"))

        localBroadcastManager.sendBroadcast(Intent(IN_LOBBY_BROADCAST).apply { putExtra("lobby", lobbyStr) })
    } }

    fun leaveLobby() = GlobalScope.launch { mutex.withLock {
        val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "leave_lobby",
            |"lobby_id": $lobbyId}""".trimMargin()

        writer.write(request)
        writer.flush()

        val response = mapper.readTree(reader.readLine())
        status = response.get("status").asText()
        lobbyId = -1

        assert(status == "SELECTING_LOBBY")

        val lobbiesStr = mapper.writeValueAsString(response.get("lobbies_list"))

        localBroadcastManager.sendBroadcast(Intent(LOBBIES_LIST_BROADCAST).apply { putExtra("lobbies_list", lobbiesStr) })
    } }

    fun make_move(positions : IntArray) = GlobalScope.launch { mutex.withLock {
        val request = """{"status": "$status",
            |"player_id": $playerId,
            |"action": "make_move",
            |"lobby_id": $lobbyId,
            |"game_id": $gameId,
            |"move_positions": ${mapper.writeValueAsString(positions)}}""".trimMargin()

        writer.write(request)
        writer.flush()
        // we get no response after this
    } }

    // send default handshake and get player id and list of lobbies
    private suspend fun init() = mutex.withLock {
        val request = """{"status": "$status"}"""

        writer.write(request)
        writer.flush()

        val response = mapper.readTree(reader.readLine())
        playerId = response.get("player_id").asInt()
        status = response.get("status").asText()
        val lobbiesStr = mapper.writeValueAsString(response.get("lobbies_list"))

        localBroadcastManager.sendBroadcast(Intent(LOBBIES_LIST_BROADCAST).apply { putExtra("lobbies_list", lobbiesStr) })
    }

    suspend fun connect() {
        init()

        while (true) {
            if (reader.ready()) {
                if (status == "SELECTING_LOBBY") {
                    assert(false)
                } else if (status == "IN_LOBBY") {
                    val tmp_str = reader.readLine();
                    Log.d("TG_connect_loop", tmp_str)

                    val update = mapper.readTree(tmp_str)

                    status = update.get("status").asText()

                    if (status == "IN_GAME") {

                        gameId = update.get("game_id").asInt()
                        val gameStr = mapper.writeValueAsString(update.get("game"))
                        localBroadcastManager.sendBroadcast(Intent("GO_TO_GAME").apply { putExtra("game", gameStr) })
                    } else {
                        val lobbyStr = mapper.writeValueAsString(update.get("lobby"))
                        localBroadcastManager.sendBroadcast(Intent(IN_LOBBY_BROADCAST).apply { putExtra("lobby", lobbyStr) })
                    }
                } else if (status == "IN_GAME") {
                    val update = mapper.readTree(reader.readLine())

                    status = update.get("status").asText()

                    if (status == "GAME_ENDED") {
                        localBroadcastManager.sendBroadcast(Intent("GO_TO_SCORE"))
                        continue
                    }

                    val gameStr = mapper.writeValueAsString(update.get("game"))
                    localBroadcastManager.sendBroadcast(Intent(IN_GAME_BROADCAST).apply { putExtra("game", gameStr) })
                }
            }
        }
    }

    fun ready() = reader.ready()

    override fun close() = socket.close()
}

class MultiplayerGameActivity : AppCompatActivity(), GameInterface {
    lateinit var connector : Connector
        private set

    override fun showScore(score: Int) {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameScoreFragment.newInstance(score), SCORE_FRAGMENT_TAG); commit() }
    }

    override fun startGame() {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameFragment()); commit() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        GlobalScope.launch {
            connector = Connector(this@MultiplayerGameActivity)
            connector.connect()
            connector.close()
        }

        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, LobbySelectionFragment()); commit() }

        LocalBroadcastManager.getInstance(this).registerReceiver(object :BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val gameStr = intent!!.extras!!.getString("game")
                supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameFragment.newInstance(gameStr)); commit() }
                //LocalBroadcastManager.getInstance(this@MultiplayerGameActivity).sendBroadcast(Intent(connector.IN_GAME_BROADCAST).apply { putExtra("game", gameStr) })
            }
        }, IntentFilter("GO_TO_GAME"))

        LocalBroadcastManager.getInstance(this).registerReceiver(object :BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameScoreFragment.newInstance(0)); commit() }
            }
        }, IntentFilter("GO_TO_SCORE"))
    }
}