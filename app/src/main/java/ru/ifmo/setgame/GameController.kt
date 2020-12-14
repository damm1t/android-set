package ru.ifmo.setgame

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import androidx.lifecycle.MutableLiveData
import java.util.*


private const val DEFAULT_COLUMNS = 3
private const val DEFAULT_ROWS = 4
private const val CARDS_IN_SET = 3

class GameController(private val viewCallback: ViewCallback) {
    val rowCount: Int = DEFAULT_ROWS
    val columnCount: Int = DEFAULT_COLUMNS
    private val cardsInSet: Int = CARDS_IN_SET

    private var score = 0
    private var computerScore = 0
    private var setOnBoard = IntArray(3)

    var isMultiplayer = false
    var isComputer = false

    private var connector: Connector? = null
    private lateinit var timerComp: Timer

    var timerGlobalStart: Long = 0
    var timerGlobalFinish: Long = 0

    private val boardLiveData = MutableLiveData<MutableList<PlayingCard>>()
    private val deckLiveData = MutableLiveData<MutableList<PlayingCard>>()

    fun getBoardLiveData(): MutableLiveData<MutableList<PlayingCard>> = boardLiveData
    fun getDeckLiveData(): MutableLiveData<MutableList<PlayingCard>> = deckLiveData

    init {
        deckLiveData.value = loadDefaultDeck()
        boardLiveData.value = mutableListOf<PlayingCard>()
    }

    interface ViewCallback {
        fun onBoardUpdated(board: MutableList<PlayingCard>)
        fun getStringById(resId: Int): String
        fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray)
        fun vibrate(int: Int)
    }

    fun shuffleDeck() {
        deckLiveData.value?.shuffle()
    }

    fun getTopDeck(): PlayingCard {
        return deckLiveData.value?.get(0)!!
    }

    fun getCard(i: Int): PlayingCard? {
        return boardLiveData.value?.get(i)
    }

    fun onSelectCard(i: Int) {
        boardLiveData.value?.get(i)?.selected  = !boardLiveData.value?.get(i)?.selected!!
    }

    // ToDo wat is update
    fun updateBoard() {
        deckLiveData.value?.get(0)?.let { boardLiveData.value?.add(it) }
        deckLiveData.value?.removeAt(0)
    }

    fun setTimer() {
        timerComp = Timer()
    }

    fun setConnector(connector: Connector) {
        this.connector = connector
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

        if (selected.size != cardsInSet)
            return

        if (isMultiplayer) {
            connector!!.make_move(selected)
        } else {
            for (i in selected) {
                deckLiveData.value?.get(0)?.let { boardLiveData.value?.set(i, it) }
                if (deckLiveData.value?.size ?: 0 > 1) deckLiveData.value?.removeAt(0)
            }
            var iterations = 5
            while (iterations-- != 0 && !hasSets()) {
                Log.d("GameController", "set not found, reshuffle deck")
                for (i in selected) {
                    boardLiveData.value?.get(i)?.let { deckLiveData.value?.add(it) }
                    deckLiveData.value?.get(0)?.let { boardLiveData.value?.set(i, it) }
                    if (deckLiveData.value?.size ?: 0 > 1) deckLiveData.value?.removeAt(0)
                }
            }

            boardLiveData.setValue(boardLiveData.value)

            if (!hasSets()) {
                timerGlobalFinish = System.currentTimeMillis()
                val playersArray = if (isComputer) {
                    arrayOf(viewCallback.getStringById(R.string.player_you),
                            viewCallback.getStringById(R.string.player_computer))
                } else {
                    arrayOf(viewCallback.getStringById(R.string.player_you))
                }
                val scoresArray =
                        if (isComputer) {
                            intArrayOf(score, computerScore)
                        } else {
                            intArrayOf(score)
                        }
                val title =
                        if (isComputer) viewCallback.getStringById(R.string.singleplayer_over)
                        else viewCallback.getStringById(R.string.training_over)
                viewCallback.showScore(title, (timerGlobalFinish - timerGlobalStart) / 1000,
                        playersArray, scoresArray)
            }
        }
    }

    fun drawBoardFromJSON(json: String) {
        val objectMapper = jacksonObjectMapper()
        val jsonBoard = objectMapper.readTree(json).get("board")

        for (i in 0 until 12) {
            val features = objectMapper.treeToValue<IntArray>(jsonBoard.get(i.toString()))
            if (features != null) {
                boardLiveData.value?.set(i, PlayingCard(features))
            } else {
                Log.d("GameController", "Could not get data for card $i")
                boardLiveData.value?.set(i, PlayingCard(intArrayOf(), isValid = false))
            }
        }

        viewCallback.onBoardUpdated(boardLiveData.value!!) //drawBoard()
    }


    fun checkSets() {
        val selectedCount = boardLiveData.value?.count { it.selected }
        val propertiesSize = boardLiveData.value?.get(0)?.properties?.size

        if (selectedCount == cardsInSet) {
            val properties = propertiesSize?.let { Array(it) { mutableListOf<Int>() } }

            for (card in boardLiveData.value!!) {
                if (card.selected) {
                    for (i in 0 until propertiesSize!!) {
                        properties?.get(i)?.add(card.properties[i])
                    }
                }
            }

            if (properties != null) {
                if (properties.all { prop -> prop.distinct().let { it.size == 1 || it.size == cardsInSet } }) {
                    score++
                    val changedBoardId = mutableListOf<Int>()
                    for (i in 0 until columnCount * rowCount) {
                        if (boardLiveData.value!![i].selected) {
                            changedBoardId.add(i)
                        }
                    }
                    makeMove(changedBoardId.toIntArray())
                }
                else {
                    viewCallback.vibrate(1)
                    boardLiveData.value!!.forEach({ it -> it.selected = false})
                    viewCallback.onBoardUpdated(boardLiveData.value!!)
                }
            }
        }
    }

    private fun hasSets(): Boolean {
        if (deckLiveData.value?.size ?: 0 == 1) return false

        var has = false
        val propertiesSize = boardLiveData.value?.get(0)?.properties?.size

        for (i in 0 until columnCount * rowCount)
            for (j in i + 1 until columnCount * rowCount)
                for (k in j + 1 until columnCount * rowCount) {
                    val properties = propertiesSize?.let { Array(it) { mutableListOf<Int>() } }

                    boardLiveData.value?.get(i)?.let {
                        for (t in 0 until propertiesSize!!) {
                            properties?.get(t)?.add(it.properties[t])
                        }
                    }

                    boardLiveData.value?.get(j)?.let {
                        for (t in 0 until propertiesSize!!) {
                            properties?.get(t)?.add(it.properties[t])
                        }
                    }

                    boardLiveData.value?.get(k)?.let {
                        for (t in 0 until propertiesSize!!) {
                            properties?.get(t)?.add(it.properties[t])
                        }
                    }

                    if (properties != null) {
                        if (properties.all { prop -> prop.distinct().let { it.size == 1 || it.size == cardsInSet } }) {
                            has = true
                            setOnBoard[0] = i
                            setOnBoard[1] = j
                            setOnBoard[2] = k
                        }
                    }
                }

        return has
    }
}