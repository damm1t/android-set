package ru.ifmo.setgame

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.ifmo.setgame.network.Connector
import java.util.Timer
import java.util.TimerTask

private const val DEFAULT_COLUMNS = 3
private const val DEFAULT_ROWS = 4
private const val CARDS_IN_SET = 3

class GameController constructor(
        private val needToShuffle: Boolean = true) {
    val rowCount: Int = DEFAULT_ROWS
    val columnCount: Int = DEFAULT_COLUMNS
    private val cardsInSet: Int = CARDS_IN_SET

    private var score = 0
    private var computerScore = 0
    private var setOnBoard = IntArray(3)

    var isMultiplayer = false
    var isComputer = false

    private var connector: Connector? = null
    private var viewCallback: ViewCallback? = null

    private lateinit var timerComp: Timer
    var timerGlobalStart: Long = 0
    var timerGlobalFinish: Long = 0

    private val boardLiveData = MutableLiveData<MutableList<PlayingCard>>()
    private val deck = loadDefaultDeck()

    private val connectorGameObserver = Observer<String> { json -> createBoardFromJSON(json) }

    fun getBoardLiveData(): LiveData<MutableList<PlayingCard>> = boardLiveData

    fun getDeckSize(): Int {
        return deck.size
    }

    init {
        initBoard()
    }

    interface ViewCallback {
        fun showScore(againstComputer: Boolean, time: Long, scores: IntArray)
        fun vibrate(int: Int)
    }

    private fun initBoard() {
        boardLiveData.value = mutableListOf<PlayingCard>()

        if (needToShuffle) {
            deck.shuffle()
        }

        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                deck[0].let { boardLiveData.value?.add(it) }
                deck.removeAt(0)
            }
        }

        boardLiveData.value = boardLiveData.value
    }

    fun getCard(i: Int): PlayingCard? {
        return boardLiveData.value?.get(i)
    }

    fun onSelectCard(i: Int) {
        boardLiveData.value?.get(i)?.selected = !boardLiveData.value?.get(i)?.selected!!
    }

    fun setTimer() {
        timerComp = Timer()
    }

    fun setConnector(connector: Connector) {
        this.connector = connector
        connector.getGameLiveData().observeForever(connectorGameObserver)
    }

    fun removeConnector() {
        connector?.getGameLiveData()?.removeObserver(connectorGameObserver)
        this.connector = null
    }

    fun setViewCallback(viewCallback: ViewCallback) {
        this.viewCallback = viewCallback
    }

    fun removeViewCallback() {
        this.viewCallback = null
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
                boardLiveData.value?.set(i, deck[0])
                if (deck.size > 1) deck.removeAt(0)
            }
            var iterations = 5
            while (iterations-- != 0 && !hasSets()) {
                Log.d("GameController", "set not found, reshuffle deck")
                for (i in selected) {
                    boardLiveData.value?.get(i)?.let { deck.add(it) }
                    boardLiveData.value?.set(i, deck[0])
                    if (deck.size > 1) deck.removeAt(0)
                }
            }

            boardLiveData.setValue(boardLiveData.value)

            if (!hasSets()) {
                timerGlobalFinish = System.currentTimeMillis()
                val scoresArray =
                        if (isComputer) {
                            intArrayOf(score, computerScore)
                        } else {
                            intArrayOf(score)
                        }

                viewCallback?.showScore(isComputer,
                        (timerGlobalFinish - timerGlobalStart) / 1000, scoresArray)
            }
        }
    }

    fun createBoardFromJSON(json: String) {
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

        boardLiveData.value = boardLiveData.value
    }


    fun checkSets(): Boolean {
        val selectedCount = boardLiveData.value?.count { it.selected }
        val selectedIsSet: Boolean = isSet(boardLiveData.value?.filter { it -> it.selected },
                boardLiveData.value?.get(0)?.properties?.size)
        if (!selectedIsSet) {
            if (selectedCount == cardsInSet) {
                viewCallback?.vibrate(1)
                boardLiveData.value!!.forEach { it.selected = false }
                boardLiveData.value = boardLiveData.value
            }
            return false
        }
        score++
        val changedBoardId = mutableListOf<Int>()
        for (i in 0 until columnCount * rowCount) {
            if (boardLiveData.value!![i].selected) {
                changedBoardId.add(i)
            }
        }
        makeMove(changedBoardId.toIntArray())
        return true
    }

    @VisibleForTesting
    fun isSet(cards: List<PlayingCard?>?, propertiesSize: Int?): Boolean {
        if (cards != null) {
            if (cards.size != cardsInSet) {
                return false
            }
        }

        val properties = propertiesSize?.let { Array(it) { mutableListOf<Int>() } }

        for (card in boardLiveData.value!!) {
            if (card.selected) {
                for (i in 0 until propertiesSize!!) {
                    properties?.get(i)?.add(card.properties[i])
                }
            }
        }

        if (properties == null) {
            return false
        }

        return (properties.all { prop -> prop.distinct().let { it.size == 1 || it.size == cardsInSet } })
    }

    @VisibleForTesting
    private fun hasSets(): Boolean {
        if (deck.size == 1) return false

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