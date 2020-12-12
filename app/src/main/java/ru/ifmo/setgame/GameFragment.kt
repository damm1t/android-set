package ru.ifmo.setgame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.gridlayout.widget.GridLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import kotlinx.android.synthetic.main.card_frame.view.*
import kotlinx.android.synthetic.main.fragment_game.view.*
import ru.ifmo.setgame.R.drawable.card_frame_drawable
import ru.ifmo.setgame.R.layout.card_frame
import ru.ifmo.setgame.R.layout.fragment_game
import java.util.*

class GameFragment : androidx.fragment.app.Fragment() {
    private val DEFAULT_COLUMNS = 3
    private val DEFAULT_ROWS = 4
    private val CARDS_IN_SET = 3

    private val board = mutableListOf<PlayingCard>()
    private val images = mutableListOf<FrameLayout>()

    private val deck = loadDefaultDeck()
    private var score = 0
    private var computerScore = 0
    private lateinit var gameView: View
    private var setOnBoard = IntArray(3)

    private var isMultiplayer = false
    private var isComputer = false
    private var allowCustomCards = false

    private lateinit var timerComp: Timer
    private var timerGlobalStart: Long = 0
    private var timerGlobalFinish: Long = 0

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val json = intent?.extras?.getString("game")!!
            drawBoardFromJSON(json)
        }
    }

    override fun onStart() {
        super.onStart()
        if (isMultiplayer) {
            LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver, IntentFilter(IN_GAME_BROADCAST))
        }
        if (isComputer) {
            timerComp.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    Handler(Looper.getMainLooper()).post { makeMove(setOnBoard) }
                    computerScore++
                }
            }, 10_000, 10_000)
        }
    }

    override fun onStop() {
        if (isMultiplayer) {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)
        }
        if (isComputer) {
            timerComp.cancel()
        }
        super.onStop()
    }

    // if multiplayer game sends move to server
    // otherwise makes move locally
    fun makeMove(selected: IntArray) {
        if (selected.size != 3)
            return

        if (isMultiplayer) {
            (activity as GameActivity).connector.make_move(selected)
        } else {
            for (i in selected) {
                board[i] = deck[0]
                if (deck.size > 1) deck.removeAt(0)
            }
            var iterations = 5
            while (iterations-- != 0 && !hasSets()) {
                Log.d("tg", "set not found, reshuffle deck")
                for (i in selected) {
                    deck.add(board[i])
                    board[i] = deck[0]
                    if (deck.size > 1) deck.removeAt(0)
                }
            }
            drawBoard()

            if (!hasSets()) {
                timerGlobalFinish = System.currentTimeMillis()
                val playersArray = if (isComputer) { arrayOf(getString(R.string.player_you), getString(R.string.player_computer)) } else { arrayOf(getString(R.string.player_you)) }
                val scoresArray = if (isComputer) { intArrayOf(score, computerScore) } else { intArrayOf(score) }
                val title = if (isComputer) getString(R.string.singleplayer_over) else getString(R.string.training_over)
                (activity as GameActivity).showScore(title, (timerGlobalFinish - timerGlobalStart) / 1000, playersArray, scoresArray)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        gameView = inflater.inflate(fragment_game, container, false)

        gameView.game_grid.rowCount = DEFAULT_ROWS
        gameView.game_grid.columnCount = DEFAULT_COLUMNS

        deck.shuffle()

        for (i in 0 until DEFAULT_ROWS) {
            for (j in 0 until DEFAULT_COLUMNS) {
                val params = GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.FILL, 1f), GridLayout.spec(j, GridLayout.FILL, 1f))
                params.width = 0
                params.height = 0

                val image = inflater.inflate(card_frame, gameView.game_grid, false) as FrameLayout
                image.card_image.setImageDrawable(deck[0].getDrawable(resources, allowCustomCards))
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


        arguments?.apply {
            isMultiplayer = getBoolean("multiplayer")
            isComputer = getBoolean("computer")

            if (isComputer) {
                timerGlobalStart = System.currentTimeMillis()

                timerComp = Timer()
            }

            if (isMultiplayer) {
                drawBoardFromJSON(getString("json")!!)
            }
        }

        allowCustomCards = activity!!.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(PREFERENCE_CUSTOM_CARDS, false)

        return gameView
    }


    private fun checkSets() {
        val selectedCount = board.count { it.selected }
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

    private fun drawBoardFromJSON(json: String) {
        val objectMapper = jacksonObjectMapper()
        val jsonBoard = objectMapper.readTree(json).get("board")

        for (i in 0 until 12) {
            val features = objectMapper.treeToValue<IntArray>(jsonBoard.get(i.toString()))
            if (features != null) {
                board[i] = PlayingCard(features)
            } else {
                Log.d("GameFragment", "Could not get data for card $i")
                board[i] = PlayingCard(intArrayOf(), isValid = false)
            }
        }

        drawBoard()
    }

    private fun drawBoard() {
        for (i in 0 until 12) {
            images[i].card_image.setImageDrawable(board[i].getDrawable(resources, allowCustomCards))
            images[i].card_frame.visibility = ImageView.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(json: String, isMultiplayer: Boolean, isComputer: Boolean) =
                GameFragment().apply {
                    arguments = Bundle().apply {
                        putString("json", json)
                        putBoolean("multiplayer", isMultiplayer)
                        putBoolean("computer", isComputer)
                    }
                }
    }
}