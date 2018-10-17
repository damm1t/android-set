package ru.ifmo.setgame

import android.graphics.Color
import android.graphics.PorterDuff
import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import kotlinx.android.synthetic.main.card_frame.*
import kotlinx.android.synthetic.main.card_frame.view.*
import kotlinx.android.synthetic.main.fragment_game.view.*

class GameFragment : Fragment() {

    private val DEFAULT_COLUMNS = 3
    private val DEFAULT_ROWS = 4

    private val board = mutableListOf<PlayingCard>()
    private val images = mutableListOf<FrameLayout>()

    private val deck = loadDefaultDeck()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        view.game_grid.rowCount = DEFAULT_ROWS
        view.game_grid.columnCount = DEFAULT_COLUMNS

        deck.shuffle()
        for (i in 0 until DEFAULT_ROWS) {
            for (j in 0 until DEFAULT_COLUMNS) {
                val params = GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.FILL, 1f), GridLayout.spec(j,GridLayout.FILL,1f))
                params.width = 0
                params.height = 0

                val image = inflater.inflate(R.layout.card_frame, view.game_grid, false) as FrameLayout
                image.card_image.setImageDrawable(ResourcesCompat.getDrawable(resources, deck[0].drawable_id, null))
                image.card_frame.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.card_frame_drawable, null))

                image.setOnClickListener {
                    val index = i * DEFAULT_COLUMNS + j
                    board[index].selected = !board[index].selected

                    it.card_frame.visibility = if (board[index].selected) ImageView.VISIBLE else ImageView.GONE

                    Log.d("TG", index.toString())
                    checkSets()
                }

                images.add(image)
                board.add(deck[0])
                deck.removeAt(0)

                view.game_grid.addView(image, params)
            }
        }

        return view
    }

    private fun checkSets() {
        var selectedCount = 0
        var propertiesSize = 0

        for (card in board) {
            if (card.selected) {
                selectedCount++
                propertiesSize = card.properties.size
            }
        }

        if (selectedCount == 3) {
            val properties = Array(propertiesSize){ mutableListOf<Int>() }
            for (card in board) {
                if (!card.selected) { continue }
                for (i in 0 until propertiesSize) {
                    properties[i].add(card.properties[i])
                }
            }

            if (properties.map { prop -> prop.distinct().let { it.size==1 || it.size == 3 } }.all { it }) {
                for (i in 0 until DEFAULT_COLUMNS * DEFAULT_ROWS) {
                    if (board[i].selected) {
                        board[i] = deck[0]
                        images[i].card_image.setImageDrawable(ResourcesCompat.getDrawable(resources, deck[0].drawable_id, null))
                        images[i].card_frame.visibility = ImageView.GONE
                        if (deck.size > 1) deck.removeAt(0) // check so we never crash because of pulling from empty deck
                    }
                }
            }
        }
    }
}
