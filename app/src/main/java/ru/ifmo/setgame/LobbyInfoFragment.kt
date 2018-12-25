package ru.ifmo.setgame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.fragment_lobby_info.*
import kotlinx.android.synthetic.main.fragment_lobby_info.view.*

class LobbyInfoFragment : Fragment() {
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val json = intent?.extras?.getString("lobby")!!
            setDataFromJsonString(json, view!!)
        }
    }

    fun setDataFromJsonString(string: String, fragment_view: View) {
        val lobby = jacksonObjectMapper().readValue<Lobby>(string)
        val playersArray = lobby.in_lobby

        info_lobby_name.text = getString(R.string.lobby_name_placeholder, lobby.lobby_id)
        info_max_players.text = getString(R.string.players_count_placeholder_long, playersArray.size, lobby.max_players)
        players_in_lobby.removeAllViews()

        info_lobby_name.visibility = View.VISIBLE
        info_max_players.visibility = View.VISIBLE
        players_in_lobby.visibility = View.VISIBLE
        btn_leave.visibility = View.VISIBLE
        info_prograss_bar.visibility = View.GONE

        for (player in playersArray) {
            val tv = TextView(context).apply {
                text = getString(R.string.player_name_placeholder, player)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }
            fragment_view.players_in_lobby.addView(tv)
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcastReceiver, IntentFilter("ru.ifmo.setgame.IN_LOBBY"))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lobby_info, container, false)

        arguments?.apply {
            if (getBoolean("create")) {
                (activity as GameActivity).connector.createLobby(getInt("max_players"))
            } else {
                (activity as GameActivity).connector.joinLobby(getInt("lobby_id"))
            }
        }

        view.btn_leave.setOnClickListener {
            (activity as GameActivity).connector.leaveLobby()
            (activity as GameActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.game_fragment, LobbySelectionFragment()).commit()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstanceJoin(lobbyId: Int) =
                LobbyInfoFragment().apply {
                    arguments = Bundle().apply {
                        putInt("lobby_id", lobbyId)
                        putBoolean("create", false)
                    }
                }

        @JvmStatic
        fun newInstanceCreate(maxPlayers: Int) =
                LobbyInfoFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean("create", true)
                        putInt("max_players", maxPlayers)
                    }
                }
    }
}