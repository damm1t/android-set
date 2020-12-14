package ru.ifmo.setgame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.card_frame.view.*
import kotlinx.android.synthetic.main.fragment_game.view.*
import ru.ifmo.setgame.R.drawable.card_frame_drawable
import ru.ifmo.setgame.R.layout.card_frame
import ru.ifmo.setgame.R.layout.fragment_game

class GameFragment : androidx.fragment.app.Fragment(), GameController.ViewCallback {

    private val images = mutableListOf<FrameLayout>()
    private lateinit var controller: GameController
    private lateinit var gameView: View
    private lateinit var viewModel : GameViewModel
    private var allowCustomCards = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val json = intent?.extras?.getString("game")!!
            controller.drawBoardFromJSON(json)
        }
    }

    override fun onStart() {
        super.onStart()
        if (controller.isMultiplayer) {
            LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver, IntentFilter(IN_GAME_BROADCAST))
            controller.setConnector((activity as GameActivity).connector)
        }
        if (controller.isComputer) {
            controller.scheduleRate()
        }
    }

    override fun onStop() {
        if (controller.isMultiplayer) {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver)
            controller.removeConnector()
        }
        if (controller.isComputer) {
            controller.stopRate()
        }
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        controller = GameController(this)
        viewModel = GameViewModel(controller)

        gameView = inflater.inflate(fragment_game, container, false)

        gameView.game_grid.rowCount = controller.rowCount
        gameView.game_grid.columnCount = controller.columnCount

        controller.shuffleDeck()

        for (i in 0 until controller.rowCount) {
            for (j in 0 until controller.columnCount) {
                val params = GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.FILL, 1f), GridLayout.spec(j, GridLayout.FILL, 1f))
                params.width = 0
                params.height = 0

                val image = inflater.inflate(card_frame, gameView.game_grid, false) as FrameLayout
                image.card_image.setImageDrawable(controller.getTopDeck().getDrawable(resources, allowCustomCards))
                image.card_frame.setImageDrawable(ResourcesCompat.getDrawable(resources, card_frame_drawable, null))

                image.setOnClickListener {
                    val index = i * controller.columnCount + j
                    controller.onSelectCard(index)

                    it.card_frame.visibility = if (controller.getCard(index)?.selected!!) ImageView.VISIBLE else ImageView.GONE

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
            controller.timerGlobalStart = System.currentTimeMillis()

            if (controller.isComputer) {
                controller.setTimer()
            }

            if (controller.isMultiplayer) {
                //todo investigate where we need to set connector
                controller.setConnector((activity as GameActivity).connector)
                controller.drawBoardFromJSON(getString("json")!!)
            }
        }

        allowCustomCards = activity!!.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(PREFERENCE_CUSTOM_CARDS, false)

        viewModel.getBoard().observe(activity!!, Observer {
            onBoardUpdated(controller.getBoardLiveData().value!!)
        })

        viewModel.getDeck().observe(activity!!, Observer {
            onBoardUpdated(controller.getBoardLiveData().value!!)
        })

        return gameView
    }

    override fun vibrate(secs : Int) {
        val vibrator : Vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val canVibrate: Boolean = vibrator.hasVibrator()
        val milliseconds = 1000L * secs

        if (canVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26
                vibrator.vibrate(
                        VibrationEffect.createOneShot(
                                milliseconds,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                )
            } else {
                // This method was deprecated in API level 26
                vibrator.vibrate(milliseconds)
            }
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
            images[i].card_image.setImageDrawable(board[i].getDrawable(resources, allowCustomCards))
            images[i].card_frame.visibility = ImageView.GONE
        }
    }

    override fun getStringById(resId: Int): String {
        return getString(resId)
    }

    override fun showScore(title: String, time: Long, players: Array<String>, scores: IntArray) {
        (activity as GameActivity).gameNavigation.showScore(title, time, players, scores)
    }
}
