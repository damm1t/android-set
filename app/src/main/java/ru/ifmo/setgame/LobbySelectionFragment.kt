package ru.ifmo.setgame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.android.synthetic.main.fragment_lobby_selection.*
import kotlinx.android.synthetic.main.fragment_lobby_selection.view.*
import kotlinx.android.synthetic.main.item_lobby.view.*
import java.text.FieldPosition

class LobbySelectionFragment : Fragment() {

    class LobbyAdapter(var data: Array<Lobby>) : RecyclerView.Adapter<LobbyAdapter.VH>() {

        class VH(tv: View) : RecyclerView.ViewHolder(tv) {
            val name = tv.lobby_name!!
            val nOfPlayers = tv.lobby_n_players!!
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val tv = LayoutInflater.from(parent.context).inflate(R.layout.item_lobby, parent, false)
            return VH(tv)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.nOfPlayers.text = "${data[position].in_lobby.size} / ${data[position].max_players}"
            holder.name.text = data[position].lobby_id.toString()
        }
    }

    lateinit var adapter: LobbyAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lobby_selection, container, false)

        adapter = LobbyAdapter(arrayOf())

        view.recycler_view_lobbies.adapter = adapter
        view.recycler_view_lobbies.layoutManager = LinearLayoutManager(activity)
        view.recycler_view_lobbies.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        LocalBroadcastManager.getInstance(context!!).registerReceiver(object :BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val str = intent?.extras?.getString("lobbies")!!
                val lobbies = jacksonObjectMapper().readValue<List<Lobby>>(str)
                adapter.data = lobbies.toTypedArray()
                adapter.notifyDataSetChanged()
            }
        }, IntentFilter("ru.ifmo.setgame.LOBBIES_LIST"))
        return view
    }
}