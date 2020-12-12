package ru.ifmo.setgame

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.*


const val DEFAULT_COLUMNS = 3
const val DEFAULT_ROWS = 4
const val CARDS_IN_SET = 3


class GameController(val rowCount: Int, val columnCount: Int, private val viewCallback: ViewCallback) {
    val cardsInSet = 3
    private val board = mutableListOf<PlayingCard>()
    val deck = loadDefaultDeck()
    var score = 0
    var computerScore = 0
    var setOnBoard = IntArray(3)

    var isMultiplayer = false
    var isComputer = false
    var allowCustomCards = false
    private var connector: Connector? = null
    private lateinit var timerComp: Timer
    var timerGlobalStart: Long = 0
    var timerGlobalFinish: Long = 0


    interface ViewCallback {
        fun onBoardUpdated(board: MutableList<PlayingCard>)
        fun onGetString(resId: Int): String
        fun onShowScore(title: String, time: Long, players: Array<String>, scores: IntArray)
    }

    fun setCard(i: Int, value: PlayingCard) {
        board[i] = value
    }

    fun getCard(i: Int): PlayingCard {
        return board[i]
    }

    fun onSelectCard(i: Int) {
        board[i].selected = !board[i].selected
    }

    fun updateBoard() {
        board.add(deck[0])
        deck.removeAt(0)
    }

    fun setTimer() {
        timerComp = Timer()
    }

    fun setConnector(activity: Activity) {
        this.connector = (activity as GameActivity).connector
    }

    fun removeConnector() {
        this.connector = null
    }

    fun scheduleRate() {
        timerComp.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post { makeMove(setOnBoard) }
                computerScore++
            }
        }, 10_000, 10_000)
    }

    fun stopRate() {
        timerComp.cancel()
    }

    // if multiplayer game sends move to server
    // otherwise makes move locally
    fun makeMove(selected: IntArray) {

        if (selected.size != 3)
            return

        if (isMultiplayer) {
            connector!!.make_move(selected)
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
            viewCallback.onBoardUpdated(board) //drawBoard()

            if (!hasSets()) {
                timerGlobalFinish = System.currentTimeMillis()
                val playersArray = if (isComputer) {
                    arrayOf(viewCallback.onGetString(R.string.player_you), viewCallback.onGetString(R.string.player_computer))
                } else {
                    arrayOf(viewCallback.onGetString(R.string.player_you))
                }
                val scoresArray = if (isComputer) {
                    intArrayOf(score, computerScore)
                } else {
                    intArrayOf(score)
                }
                val title = if (isComputer) viewCallback.onGetString(R.string.singleplayer_over) else viewCallback.onGetString(R.string.training_over)
                viewCallback.onShowScore(title, (timerGlobalFinish - timerGlobalStart) / 1000, playersArray, scoresArray)
                //(activity as GameActivity).showScore(title, (timerGlobalFinish - timerGlobalStart) / 1000, playersArray, scoresArray)
            }
        }
    }


    fun checkSets() {
        val selectedCount = board.count { it.selected }
        val propertiesSize = board[0].properties.size

        if (selectedCount == cardsInSet) {
            val properties = Array(propertiesSize) { mutableListOf<Int>() }

            for (card in board) {
                if (card.selected) {
                    for (i in 0 until propertiesSize) {
                        properties[i].add(card.properties[i])
                    }
                }
            }

            if (properties.all { prop -> prop.distinct().let { it.size == 1 || it.size == cardsInSet } }) {
                score++
                val changedBoardId = mutableListOf<Int>()
                for (i in 0 until columnCount * rowCount) {
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

        for (i in 0 until columnCount * rowCount)
            for (j in i + 1 until columnCount * rowCount)
                for (k in j + 1 until columnCount * rowCount) {
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

                    if (properties.all { prop -> prop.distinct().let { it.size == 1 || it.size == cardsInSet } }) {
                        has = true
                        setOnBoard[0] = i
                        setOnBoard[1] = j
                        setOnBoard[2] = k
                    }
                }

        return has
    }
}