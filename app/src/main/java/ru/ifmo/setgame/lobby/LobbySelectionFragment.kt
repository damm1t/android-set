package ru.ifmo.setgame.lobby

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.android.synthetic.main.dialog_create_lobby.view.*
import kotlinx.android.synthetic.main.fragment_lobby_selection.view.*
import ru.ifmo.setgame.GameActivity
import ru.ifmo.setgame.Lobby
import ru.ifmo.setgame.R

class LobbyCreationDialog(private val viewModel: LobbyInfoViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_create_lobby, null)
        view.max_players_picker.minValue = 2 // cannot set these from xml :(
        view.max_players_picker.maxValue = 10
        view.max_players_picker.value = 2 // default value

        return AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(getString(R.string.btn_create_text)) { dialogInterface: DialogInterface, id: Int ->
                    view.progress_bar.visibility = View.VISIBLE
                    view.max_players_picker.visibility = View.INVISIBLE
                    view.max_players_text.visibility = View.INVISIBLE

                    viewModel.createLobby(view.max_players_picker.value)
                }.create()
    }
}

class LobbySelectionFragment : Fragment() {
    private lateinit var viewModel: LobbyInfoViewModel

    private lateinit var adapter: LobbyListAdapter

    private fun setLobbiesList(lobbiesList: Array<Lobby>) {
        adapter.data = lobbiesList
        adapter.notifyDataSetChanged()

        view?.swipe_refresh_lobbies?.isRefreshing = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lobby_selection, container, false)

        viewModel = LobbyInfoViewModel(
                (activity as GameActivity).connector,
                jacksonObjectMapper(),
                (activity as GameActivity).gameNavigation
        )
        adapter = LobbyListAdapter(arrayOf(), viewModel)

        view.recycler_view_lobbies.adapter = adapter
        view.recycler_view_lobbies.layoutManager = LinearLayoutManager(activity)
        view.recycler_view_lobbies.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        view.swipe_refresh_lobbies.setOnRefreshListener {
            viewModel.refreshLobbiesList()
        }

        view.fab.setOnClickListener {
            LobbyCreationDialog(viewModel).show(fragmentManager!!, "create_lobby")
        }

        viewModel.lobbiesListLiveData.observe(viewLifecycleOwner, ::setLobbiesList)

        return view
    }
}
