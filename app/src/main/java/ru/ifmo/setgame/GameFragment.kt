package ru.ifmo.setgame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.LocaleList
import androidx.core.content.res.ResourcesCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.android.synthetic.main.card_frame.view.*
import kotlinx.android.synthetic.main.fragment_game.view.*
import ru.ifmo.setgame.R.drawable.card_frame_drawable
import ru.ifmo.setgame.R.layout.card_frame
import ru.ifmo.setgame.R.layout.fragment_game
import java.util.zip.Inflater

class GameFragment : androidx.fragment.app.Fragment() {

    private val DEFAULT_COLUMNS = 3
    private val DEFAULT_ROWS = 4
    private val CARDS_IN_SET = 3

    private val board = mutableListOf<PlayingCard>()
    private val images = mutableListOf<FrameLayout>()

    private val deck = loadDefaultDeck()
    private var score = 0
    private lateinit var gameView: View
    private var setOnBoard = Array(3) { 0 }

    val receiver = object :BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val json = intent!!.extras!!.getString("game")!!
            drawBoard(json)
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver, IntentFilter("ru.ifmo.setgame.IN_GAME"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        gameView = inflater.inflate(fragment_game, container, false)

        gameView.game_grid.rowCount = DEFAULT_ROWS
        gameView.game_grid.columnCount = DEFAULT_COLUMNS

        for (i in 0 until DEFAULT_ROWS) {
            for (j in 0 until DEFAULT_COLUMNS) {
                val params = androidx.gridlayout.widget.GridLayout.LayoutParams(androidx.gridlayout.widget.GridLayout.spec(i, androidx.gridlayout.widget.GridLayout.FILL, 1f), androidx.gridlayout.widget.GridLayout.spec(j, androidx.gridlayout.widget.GridLayout.FILL, 1f))
                params.width = 0
                params.height = 0

                val image = inflater.inflate(card_frame, gameView.game_grid, false) as FrameLayout
                image.card_image.setImageDrawable(ResourcesCompat.getDrawable(resources, deck[0].drawable_id, null))
                image.card_frame.setImageDrawable(ResourcesCompat.getDrawable(resources, card_frame_drawable, null))

                image.setOnClickListener {
                    val index = i * DEFAULT_COLUMNS + j
                    board[index].selected = !board[index].selected

                    it.card_frame.visibility = if (board[index].selected) ImageView.VISIBLE else ImageView.GONE

                    Log.d("TG", index.toString())
                    checkSets()

                    /*if (!hasSets()) {
                        //(activity as GameInterface).showScore(score)
                        Log.d("TG", "No sets")
                        addRow(inflater) //ToDo incorrect update
                    }*/
                }

                images.add(image)
                board.add(deck[i * 3 + j])

                gameView.game_grid.addView(image, params)
            }
        }

        arguments?.apply {
            drawBoard(getString("json")!!)
        }

        return gameView
    }


    private fun checkSets() {
        val selectedCount = board.filter { it.selected }.size
        val propertiesSize = board[0].properties.size

        if (selectedCount == CARDS_IN_SET) {
            val selected = mutableListOf<Int>()
            for (i in 0 until 12) {
                if (board[i].selected) {
                    selected.add(i)
                }
            }

            (activity as MultiplayerGameActivity).connector.make_move(selected.toIntArray())
        }
    }

    private fun drawBoard(json : String) {
        val j_board = jacksonObjectMapper().readTree(json).get("board")
        for (i in 0 until 12) {
            val tmp = j_board.get(i.toString())
            val card_id = tmp[0].asInt() * 27 + tmp[1].asInt() * 9 + tmp[2].asInt() * 3 + tmp[3].asInt()
            images[i].card_image.setImageDrawable(ResourcesCompat.getDrawable(resources, deck[card_id].drawable_id, null))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(json: String) =
                GameFragment().apply {
                    arguments = Bundle().apply {
                        putString("json", json)
                    }
                }
    }
}