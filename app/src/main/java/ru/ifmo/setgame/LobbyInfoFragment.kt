package ru.ifmo.setgame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.fragment_lobby_info.view.*

class LobbyInfoFragment : Fragment() {
    val broadcastReceiver = object:BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val json = intent!!.extras!!.getString("lobby")!!
            setDataFromJsonString(json, view!!)
        }
    }

    fun setDataFromJsonString(string : String, fragment_view: View) {
        val lobby = jacksonObjectMapper().readTree(string)
        val playersArray = jacksonObjectMapper().readValue<IntArray>(jacksonObjectMapper().writeValueAsString(lobby.get("in_lobby"))) // TODO

        fragment_view.info_lobby_name.text = lobby.get("lobby_id").asText()
        fragment_view.info_max_players.text = "${playersArray.size} / ${lobby.get("max_players").asInt()}"

        for (player in playersArray) {
            val tv = TextView(context).apply {
                text = player.toString()
            }
            fragment_view.players_in_lobby.addView(tv)
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcastReceiver, IntentFilter("ru.ifmo.setgame.IN_LOBBY"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(broadcastReceiver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lobby_info, container, false)

        arguments?.apply {
            setDataFromJsonString(getString("lobby")!!, view)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
                LobbyInfoFragment().apply {
                    arguments = Bundle().apply { putString("lobby", param1) }
                }
    }
}