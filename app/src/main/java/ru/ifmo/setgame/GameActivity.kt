package ru.ifmo.setgame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

interface GameInterface {
    val LOBBIES_LIST_BROADCAST: String
        get() = "ru.ifmo.setgame.LOBBIES_LIST"

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

class GameActivity : AppCompatActivity(), GameInterface {
    private suspend fun connect() {
        try {
            // init everything we need
            val socket = Socket("18.222.225.249", 3691)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), "UTF-8"))
            val mapper = ObjectMapper().registerKotlinModule()

            // send default message
            val str = "{\"status\":\"NEW\"}"
            writer.write(str)
            writer.flush()

            // we get player id and list of lobbies
            val ans = reader.readLine()
            val answer = mapper.readTree(ans)
            val lobbies = answer.get("lobbies")

            // send string because it is easier than sending array of objects
            val lobbiesStr = mapper.writeValueAsString(lobbies)
            Log.d("TG",lobbiesStr)
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(LOBBIES_LIST_BROADCAST).apply { putExtra("lobbies", lobbiesStr) })

            socket.close()
        } catch (ex: Exception) {
            Log.e("TG", ex.toString())
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