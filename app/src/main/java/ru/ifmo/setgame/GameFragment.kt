package ru.ifmo.setgame

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import androidx.core.content.res.ResourcesCompat
import androidx.gridlayout.widget.GridLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import kotlinx.android.synthetic.main.card_frame.view.*
import kotlinx.android.synthetic.main.fragment_game.view.*
import ru.ifmo.setgame.R.drawable.card_frame_drawable
import ru.ifmo.setgame.R.layout.card_frame
import ru.ifmo.setgame.R.layout.fragment_game
import java.time.Clock
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {

    private val DEFAULT_COLUMNS = 3
    private val DEFAULT_ROWS = 4
    private val CARDS_IN_SET = 3

    private val board = mutableListOf<PlayingCard>()
    private val images = mutableListOf<FrameLayout>()

    private val deck = loadDefaultDeck()
    private var score = 0
    private lateinit var gameView: View
    private var setOnBoard = IntArray(3)


    private lateinit var timerComp: Timer
    private var timerGlobalStart: Long = 0
    private var timerGlobalFinish: Long = 0

    /*  private fun addRow(inflater: LayoutInflater) {
          gameView.game_grid.rowCount++
          for (i in 0 until DEFAULT_COLUMNS) {
              val params = androidx.gridlayout.widget.GridLayout.LayoutParams(androidx.gridlayout.widget.GridLayout.spec(gameView.game_grid.rowCount - 1, androidx.gridlayout.widget.GridLayout.FILL, 1f), androidx.gridlayout.widget.GridLayout.spec(i, androidx.gridlayout.widget.GridLayout.FILL, 1f))
              params.width = 0
              params.height = 0

              val image = inflater.inflate(card_frame, gameView.game_grid, false) as FrameLayout
              image.card_image.setImageDrawable(ResourcesCompat.getDrawable(resources, deck[0].drawable_id, null))
              image.card_frame.setImageDrawable(ResourcesCompat.getDrawable(resources, card_frame_drawable, null))

              image.setOnClickListener {
                  val index = DEFAULT_ROWS * DEFAULT_COLUMNS + i
                  board[index].selected = !board[index].selected

                  it.card_frame.visibility = if (board[index].selected) ImageView.VISIBLE else ImageView.GONE

                  Log.d("TG", index.toString())
                  checkSets()

              }
              images.add(image)
              board.add(deck[0])
              deck.removeAt(0)

              gameView.game_grid.addView(image, params)
          }
          gameView.game_grid.requestLayout()
      }
  */

    fun makeMove(selected: IntArray) {
        if (selected.size != 3)
            return

        for (i in selected) {
            board[i] = deck[0]
            images[i].card_image.setImageDrawable(ResourcesCompat.getDrawable(resources, deck[0].drawable_id, null))
            images[i].card_frame.visibility = ImageView.GONE
            if (deck.size > 1) deck.removeAt(0)
        }
        var iterations = 5
        while (iterations-- != 0 && !hasSets()) {
            Log.d("tg", "set not found, reshuffle deck")
            for (i in selected) {
                deck.add(board[i])
                board[i] = deck[0]
                images[i].card_image.setImageDrawable(ResourcesCompat.getDrawable(resources, deck[0].drawable_id, null))
                images[i].card_frame.visibility = ImageView.GONE
                if (deck.size > 1) deck.removeAt(0)
            }
        }

        if (!hasSets()) {
            timerGlobalFinish = System.currentTimeMillis()
            //ToDo need to return game time: timerGlobalFinish - timerGlobalStart
            (activity as GameActivity).showScore(score)
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        timerGlobalStart = System.currentTimeMillis()

        timerComp = Timer()
        timerComp.scheduleAtFixedRate(object : TimerTask() {

            override fun run() {
                makeMove(setOnBoard)

            }
        }, 10_000, 10_000)

        gameView = inflater.inflate(fragment_game, container, false)

        gameView.game_grid.rowCount = DEFAULT_ROWS
        gameView.game_grid.columnCount = DEFAULT_COLUMNS

        deck.shuffle()

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
                }

                images.add(image)
                board.add(deck[0])
                deck.removeAt(0)

                gameView.game_grid.addView(image, params)
            }
        }

        return gameView
    }


    private fun checkSets() {
        val selectedCount = board.filter { it.selected }.size
        val propertiesSize = board[0].properties.size

        if (selectedCount == CARDS_IN_SET) {
            val properties = Array(propertiesSize) { mutableListOf<Int>() }

            for (card in board) {
                if (card.selected) {
                    for (i in 0 until propertiesSize) {
                        properties[i].add(card.properties[i])
                    }
                }
            }

            if (properties.all { prop -> prop.distinct().let { it.size == 1 || it.size == CARDS_IN_SET } }) {
                score++
                val changedBoardId = mutableListOf<Int>()
                for (i in 0 until DEFAULT_COLUMNS * DEFAULT_ROWS) {
                    if (board[i].selected) {
                        changedBoardId.add(i)
                    }
                }
                makeMove(changedBoardId.toIntArray())
            }
        }
    }

    private fun hasSets(): Boolean {
        if (deck.size == 1) return false

        var has = false
        val propertiesSize = board[0].properties.size

        for (i in 0 until DEFAULT_COLUMNS * gameView.game_grid.rowCount)
            for (j in i + 1 until DEFAULT_COLUMNS * gameView.game_grid.rowCount)
                for (k in j + 1 until DEFAULT_COLUMNS * gameView.game_grid.rowCount) {
                    val properties = Array(propertiesSize) { mutableListOf<Int>() }

                    board[i].let {
                        for (t in 0 until propertiesSize) {
                            properties[t].add(it.properties[t])
                        }
                    }

                    board[j].let {
                        for (t in 0 until propertiesSize) {
                            properties[t].add(it.properties[t])
                        }
                    }

                    board[k].let {
                        for (t in 0 until propertiesSize) {
                            properties[t].add(it.properties[t])
                        }
                    }

                    if (properties.all { prop -> prop.distinct().let { it.size == 1 || it.size == CARDS_IN_SET } }) {
                        has = true
                        setOnBoard[0] = i
                        setOnBoard[1] = j
                        setOnBoard[2] = k
                    }
                }

        return has
    }
}