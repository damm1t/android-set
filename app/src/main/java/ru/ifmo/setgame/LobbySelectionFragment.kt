package ru.ifmo.setgame

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_lobby_selection.view.*
import kotlinx.android.synthetic.main.item_lobby.view.*
import java.text.FieldPosition

class LobbySelectionFragment : Fragment() {

    class LobbyAdapter(var data: List<Int>) : RecyclerView.Adapter<LobbyAdapter.VH>() {

        class VH(tv: View) : RecyclerView.ViewHolder(tv) {
            //val name = tv.lobby_name!!
            val nOfPlayers = tv.lobby_n_players!!
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val tv = LayoutInflater.from(parent.context).inflate(R.layout.item_lobby, parent, false)
            return VH(tv)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.nOfPlayers.text = "${data[position]} / 10"
        }
    }

    lateinit var adapter: LobbyAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lobby_selection, container, false)

        adapter = LobbyAdapter(listOf(1, 2, 5, 2, 2, 3))

        view.recycler_view_lobbies.adapter = adapter
        view.recycler_view_lobbies.layoutManager = LinearLayoutManager(activity)
        return view
    }
}