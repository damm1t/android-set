package ru.ifmo.setgame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.concurrent.ArrayBlockingQueue

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


    private val hostAddress = "18.222.225.249"
    private val hostPort = 3691
    private val socket = Socket(hostAddress, hostPort)
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), "UTF-8"))
    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)
    private val mapper = jacksonObjectMapper()

    private var playerId = -1
    private var lobbyId = -1

    fun requestLobbies() {
        val request = """{"status": "SELECTING_LOBBY",
            |"player_id": $playerId,
            |"action": "refresh_list"}""".trimMargin()

        writer.write(request)
        writer.flush()

        val response = mapper.readTree(reader.readLine())
        val lobbiesStr = mapper.writeValueAsString(response.get("lobbies"))

        localBroadcastManager.sendBroadcast(Intent(LOBBIES_LIST_BROADCAST).apply { putExtra("lobbies", lobbiesStr) })
    }

    // send default handshake and get player id and list of lobbies
    fun init() {
        val request = """{"status": "NEW"}"""

        writer.write(request)
        writer.flush()

        val response = mapper.readTree(reader.readLine())
        playerId = response.get("player_id").asInt()
        val lobbiesStr = mapper.writeValueAsString(response.get("lobbies"))

        localBroadcastManager.sendBroadcast(Intent(LOBBIES_LIST_BROADCAST).apply { putExtra("lobbies", lobbiesStr) })
    }

    fun ready() = reader.ready()

    override fun close() {
        socket.close()
    }
}

class MultiplayerGameActivity : AppCompatActivity(), GameInterface {
    val messages = ArrayBlockingQueue<String>(10)
    var player_id = 0


    private suspend fun connect() {
        Connector(this).use { connector ->
            connector.init()

            while (true) {
                if (connector.ready()) {
                    //Log.d("TG",reader.readLine())
                }
                if (!messages.isEmpty()) {
                    val msg = messages.take()
                    if (msg == "EXIT") {
                        break
                    } else if (msg == "REFRESH") {
                        connector.requestLobbies()
                    }
                }
            }
        }
    }

    override fun showScore(score: Int) {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameScoreFragment.newInstance(score), SCORE_FRAGMENT_TAG); commit() }
    }

    override fun startGame() {
        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, GameFragment()); commit() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        supportFragmentManager.beginTransaction().apply { replace(R.id.game_fragment, LobbySelectionFragment()); commit() }

        GlobalScope.launch {
            connect()
        }
    }
}