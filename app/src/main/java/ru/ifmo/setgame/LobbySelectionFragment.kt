package ru.ifmo.setgame

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.dialog_create_lobby.view.*
import kotlinx.android.synthetic.main.fragment_lobby_selection.view.*
import kotlinx.android.synthetic.main.item_lobby.view.*

class LobbyCreationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_create_lobby, null)
        view.max_players_picker.minValue = 1 // cannot set these from xml :(
        view.max_players_picker.maxValue = 10
        view.max_players_picker.value = 2 // default value

        return AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(getString(R.string.btn_create_text)) { dialogInterface: DialogInterface, id: Int ->
                    view.progress_bar.visibility = View.VISIBLE
                    view.max_players_picker.visibility = View.INVISIBLE
                    view.max_players_text.visibility = View.INVISIBLE

                    (activity as GameActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.game_fragment, LobbyInfoFragment.newInstanceCreate(view.max_players_picker.value))
                            .commit()
                }.create()
    }
}

class LobbySelectionFragment : Fragment() {

    class LobbyAdapter(var data: Array<Lobby>) : RecyclerView.Adapter<LobbyAdapter.VH>() {

        inner class VH(tv: View) : RecyclerView.ViewHolder(tv) {
            val name = tv.lobby_name!!
            val nOfPlayers = tv.lobby_n_players!!

            init {
                itemView.setOnClickListener{
                    (tv.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.game_fragment, LobbyInfoFragment.newInstanceJoin(
                                    data[adapterPosition].lobby_id
                            )).commit()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val tv = LayoutInflater.from(parent.context).inflate(R.layout.item_lobby, parent, false)
            return VH(tv)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val context = holder.itemView.context
            holder.nOfPlayers.text = context.getString(R.string.players_count_placeholder, data[position].in_lobby.size, data[position].max_players)
            holder.name.text = data[position].lobby_id.toString()
        }
    }

    lateinit var adapter: LobbyAdapter
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val str = intent?.extras?.getString("lobbies_list")!!
            val lobbies = jacksonObjectMapper().readValue<Array<Lobby>>(str)
            adapter.data = lobbies
            adapter.notifyDataSetChanged()

            view?.swipe_refresh_lobbies?.isRefreshing = false
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcastReceiver, IntentFilter("ru.ifmo.setgame.LOBBIES_LIST"))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lobby_selection, container, false)

        adapter = LobbyAdapter(arrayOf())

        view.recycler_view_lobbies.adapter = adapter
        view.recycler_view_lobbies.layoutManager = LinearLayoutManager(activity)
        view.recycler_view_lobbies.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        view.swipe_refresh_lobbies.setOnRefreshListener {
            (activity as GameActivity).connector.requestLobbies()
        }

        view.fab.setOnClickListener {
            LobbyCreationDialog().show(fragmentManager, "create_lobby")
        }
        return view
    }
}