package ru.ifmo.setgame.lobby

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.android.synthetic.main.fragment_lobby_info.*
import kotlinx.android.synthetic.main.fragment_lobby_info.view.*
import ru.ifmo.setgame.*

class LobbyInfoFragment : Fragment() {
    lateinit var viewModel: LobbyInfoViewModel

    private fun setLobbyInfo(lobby: Lobby) {
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
            view!!.players_in_lobby.addView(tv)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lobby_info, container, false)

        arguments?.apply {
            if (getBoolean(INTENT_KEY_SHOULD_CREATE)) {
                (activity as GameActivity).connector.createLobby(getInt(INTENT_KEY_MAX_PLAYERS))
            } else {
                (activity as GameActivity).connector.joinLobby(getInt(INTENT_KEY_LOBBY_ID))
            }
        }

        viewModel = LobbyInfoViewModel(
                (activity as GameActivity).connector,
                jacksonObjectMapper(),
                (activity as GameActivity).gameNavigation
        )

        view.btn_leave.setOnClickListener {
            viewModel.leaveLobby()
        }

        viewModel.lobbyInfoLiveData.observe(viewLifecycleOwner, ::setLobbyInfo)

        return view
    }

    companion object {
        private const val INTENT_KEY_SHOULD_CREATE = "create"
        private const val INTENT_KEY_LOBBY_ID = "lobby_id"
        private const val INTENT_KEY_MAX_PLAYERS = "max_players"

        @JvmStatic
        fun newInstanceJoin(lobbyId: Int) =
                LobbyInfoFragment().apply {
                    arguments = Bundle().apply {
                        putInt(INTENT_KEY_LOBBY_ID, lobbyId)
                        putBoolean(INTENT_KEY_SHOULD_CREATE, false)
                    }
                }

        @JvmStatic
        fun newInstanceCreate(maxPlayers: Int) =
                LobbyInfoFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(INTENT_KEY_SHOULD_CREATE, true)
                        putInt(INTENT_KEY_MAX_PLAYERS, maxPlayers)
                    }
                }
    }
}