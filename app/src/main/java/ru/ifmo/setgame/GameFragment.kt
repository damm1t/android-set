package ru.ifmo.setgame

import android.app.Activity
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

class GameFragment : androidx.fragment.app.Fragment(), GameController.ViewCallback {

    private val images = mutableListOf<FrameLayout>()
    private val controller = GameController(DEFAULT_ROWS, DEFAULT_COLUMNS, this)
    private lateinit var gameView: View


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val json = intent?.extras?.getString("game")!!
            drawBoardFromJSON(json)
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (controller.isMultiplayer) {
            controller.setConnector(activity)
        }
    }

    override fun onDetach() {
        super.onDetach()
        controller.removeConnector()
    }

    override fun onStart() {
        super.onStart()
        if (controller.isMultiplayer) {
            LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver, IntentFilter(IN_GAME_BROADCAST))
        }
        if (controller.isComputer) {
            controller.scheduleRate()
        }
    }

    override fun onStop() {
        if (controller.isMultiplayer) {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)
        }
        if (controller.isComputer) {
            controller.stopRate()
        }
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        gameView = inflater.inflate(fragment_game, container, false)

        gameView.game_grid.rowCount = controller.rowCount
        gameView.game_grid.columnCount = controller.columnCount

        controller.deck.shuffle()

        for (i in 0 until controller.rowCount) {
            for (j in 0 until controller.columnCount) {
                val params = GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.FILL, 1f), GridLayout.spec(j, GridLayout.FILL, 1f))
                params.width = 0
                params.height = 0

                val image = inflater.inflate(card_frame, gameView.game_grid, false) as FrameLayout
                image.card_image.setImageDrawable(controller.deck[0].getDrawable(resources, controller.allowCustomCards))
                image.card_frame.setImageDrawable(ResourcesCompat.getDrawable(resources, card_frame_drawable, null))

                image.setOnClickListener {
                    val index = i * controller.columnCount + j
                    controller.onSelectCard(index)

                    it.card_frame.visibility = if (controller.getCard(index).selected) ImageView.VISIBLE else ImageView.GONE

                    Log.d("TG", index.toString())
                    controller.checkSets()
                }

                images.add(image)
                controller.updateBoard()

                gameView.game_grid.addView(image, params)
            }
        }


        arguments?.apply {
            controller.isMultiplayer = getBoolean("multiplayer")
            controller.isComputer = getBoolean("computer")

            if (controller.isComputer) {
                controller.timerGlobalStart = System.currentTimeMillis()

                controller.setTimer()
            }

            if (controller.isMultiplayer) {
                drawBoardFromJSON(getString("json")!!)
            }
        }

        controller.allowCustomCards = activity!!.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(PREFERENCE_CUSTOM_CARDS, false)

        return gameView
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
            controller.setCard(i, PlayingCard(features))
        }

        drawBoard()
    }

    private fun drawBoard() {
        for (i in 0 until 12) {
            images[i].card_image.setImageDrawable(controller.getCard(i).getDrawable(resources, controller.allowCustomCards))
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

    override fun onBoardUpdated(board: MutableList<PlayingCard>) {
        for (i in 0 until 12) {
            images[i].card_image.setImageDrawable(board[i].getDrawable(resources, controller.allowCustomCards))
            images[i].card_frame.visibility = ImageView.GONE
        }
    }

    override fun onGetString(resId: Int): String {
        return getString(resId)
    }

    override fun onShowScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
        (activity as GameActivity).showScore(title, time, players, scores)
    }
}