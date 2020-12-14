package ru.ifmo.setgame.lobby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_lobby.view.*
import ru.ifmo.setgame.Lobby
import ru.ifmo.setgame.R


class LobbyListAdapter(
        var data: Array<Lobby>,
        private val viewModel: LobbyInfoViewModel
        ) : RecyclerView.Adapter<LobbyListAdapter.VH>() {
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

    inner class VH(tv: View) : RecyclerView.ViewHolder(tv) {
        val name = tv.lobby_name!!
        val nOfPlayers = tv.lobby_n_players!!

        init {
            itemView.setOnClickListener {
                viewModel.joinLobby(data[adapterPosition].lobby_id)
            }
        }
    }
}
